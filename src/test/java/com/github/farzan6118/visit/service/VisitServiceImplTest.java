package com.github.farzan6118.visit.service;

import com.github.farzan6118.appointment.dto.request.CompleteVisitRequestDto;
import com.github.farzan6118.appointment.dto.request.CreateVisitRequestDto;
import com.github.farzan6118.appointment.dto.request.RescheduleVisitRequestDto;
import com.github.farzan6118.appointment.dto.response.DurationTemplateResponseDto;
import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.appointment.repository.VisitRepository;
import com.github.farzan6118.appointment.service.DurationTemplateService;
import com.github.farzan6118.appointment.service.VisitServiceCommandImpl;
import com.github.farzan6118.clinic.model.Room;
import com.github.farzan6118.clinic.model.Clinic;
import com.github.farzan6118.clinic.service.RoomService;
import com.github.farzan6118.common.enums.VisitCategory;
import com.github.farzan6118.common.enums.VisitStatus;
import com.github.farzan6118.common.enums.VisitType;
import com.github.farzan6118.common.exception.BadRequestException;
import com.github.farzan6118.common.exception.ConflictException;
import com.github.farzan6118.common.exception.ResourceNotFoundException;
import com.github.farzan6118.common.valueobject.DateTimeRange;
import com.github.farzan6118.config.ClinicProperties;
import com.github.farzan6118.infrastructure.email.VisitNotificationService;
import com.github.farzan6118.pet.model.MedicalRecord;
import com.github.farzan6118.pet.model.Pet;
import com.github.farzan6118.pet.service.MedicalRecordService;
import com.github.farzan6118.pet.service.PetService;
import com.github.farzan6118.vet.model.Vet;
import com.github.farzan6118.vet.service.VetAvailabilityService;
import com.github.farzan6118.vet.service.VetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceImplTest {

    private final UUID petUuid = UUID.randomUUID();
    private final UUID vetUuid = UUID.randomUUID();
    private final UUID visitUuid = UUID.randomUUID();
    @Mock
    private VisitRepository visitRepository;
    @Mock
    private ClinicProperties clinicProperties;
    @Mock
    private RoomService roomService;
    @Mock
    private PetService petService;
    @Mock
    private VetService vetService;
    @Mock
    private VetAvailabilityService vetAvailabilityService;
    @Mock
    private DurationTemplateService durationTemplateService;
    @Mock
    private VisitNotificationService visitNotificationService;
    @Mock
    private MedicalRecordService medicalRecordService;
    @InjectMocks
    private VisitServiceCommandImpl service;
    private Pet pet;
    private Vet vet;
    private Room room;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setUuid(petUuid);

        vet = new Vet();
        vet.setUuid(vetUuid);

        room = new Room();
        room.setUuid(UUID.randomUUID());
        room.setName("Examination room");
        room.setActive(true);
        Clinic clinic = new Clinic();
        clinic.setActive(true);
        room.setClinic(clinic);

        lenient().when(durationTemplateService.findByName("STANDARD"))
                .thenReturn(new DurationTemplateResponseDto(
                        UUID.randomUUID(), "STANDARD", 30, "Standard visit"));
        lenient().when(vetAvailabilityService.findAvailableByUuidAndTimeRange(any(), any(), any()))
                .thenReturn(Optional.of(vet));
        lenient().when(clinicProperties.closeDays()).thenReturn(java.util.Set.of());
        lenient().when(clinicProperties.workingHours())
                .thenReturn(new ClinicProperties.WorkingHours(LocalTime.of(8, 0), LocalTime.of(17, 0)));
        lenient().when(clinicProperties.timeBlockMinutes()).thenReturn(5);
        lenient().when(roomService.getEntityByUuidForUpdate(room.getUuid())).thenReturn(room);
        lenient().when(petService.getEntityByUuidForUpdate(petUuid)).thenReturn(pet);
    }

    /**
     * Verifies that booking an onsite visit resolves the pet, veterinarian, and room,
     * creates a scheduled visit with the configured duration, and notifies participants.
     */
    @Test
    void bookVisit_shouldSaveVisitAndNotifyParticipants() {
        LocalDate visitDate = LocalDate.now().plusDays(1);
        LocalTime visitTime = LocalTime.of(10, 0);
        CreateVisitRequestDto request = new CreateVisitRequestDto(
                petUuid, vetUuid, room.getUuid(), visitDate, visitTime,
                VisitType.ONSITE, 15, "General examination");

        when(vetService.getVetWithUuidLock(vetUuid)).thenReturn(vet);
        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> {
            Visit visit = invocation.getArgument(0);
            visit.setUuid(visitUuid);
            return visit;
        });

        service.bookVisit(request);

        ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(visitCaptor.capture());

        Visit savedVisit = visitCaptor.getValue();
        assertEquals(pet, savedVisit.getPet());
        assertEquals(vet, savedVisit.getVet());
        assertEquals(room, savedVisit.getRoom());
        assertEquals(VisitStatus.SCHEDULED, savedVisit.getStatus());
        assertEquals(LocalDateTime.of(visitDate, visitTime), savedVisit.getStartTime());
        assertEquals(LocalDateTime.of(visitDate, visitTime.plusMinutes(15)), savedVisit.getEndTime());
        verify(visitNotificationService).notifyBookVisitParticipants(savedVisit, pet, vet);
    }

    /**
     * Verifies that rescheduling changes the visit time and description, reuses the
     * availability and conflict checks, saves the updated visit, and sends a notification.
     */
    @Test
    void rescheduleVisit_shouldUpdateSlotAndNotifyParticipants() {
        LocalDateTime oldStart = LocalDate.now().plusDays(1).atTime(9, 0);
        Visit visit = new Visit().schedule(
                vet, pet, room, oldStart, oldStart.plusMinutes(10), VisitType.ONSITE, "Original visit");
        visit.setUuid(visitUuid);

        LocalDate newDate = LocalDate.now().plusDays(1);
        LocalTime newTime = LocalTime.of(11, 0);
        RescheduleVisitRequestDto request = new RescheduleVisitRequestDto(
                newDate, newTime, VisitType.ONSITE, room.getUuid(),
                "Updated visit", "Owner requested another time");

        when(visitRepository.findByUuidForUpdate(visitUuid)).thenReturn(Optional.of(visit));
        when(vetService.getVetWithUuidLock(vetUuid)).thenReturn(vet);

        service.rescheduleVisit(visitUuid, request);

        assertEquals(LocalDateTime.of(newDate, newTime), visit.getStartTime());
        assertEquals(LocalDateTime.of(newDate, newTime.plusMinutes(30)), visit.getEndTime());
        assertEquals("Updated visit", visit.getDescription());
        verify(visitRepository).save(visit);
        verify(visitNotificationService).notifyRescheduleVisitParticipants(
                oldStart, visit, pet, vet, LocalDateTime.of(newDate, newTime));
    }

    /**
     * Verifies that cancelling an active visit changes its status to CANCELLED and
     * notifies the pet owner and veterinarian with the cancellation reason.
     */
    @Test
    void cancelVisit_shouldCancelVisitAndNotifyParticipants() {
        Visit visit = new Visit().schedule(
                vet, pet, room,
                LocalDate.now().plusDays(1).atTime(9, 0),
                LocalDate.now().plusDays(1).atTime(9, 20),
                VisitType.ONSITE,
                "Visit");
        visit.setUuid(visitUuid);

        when(visitRepository.findByUuid(visitUuid)).thenReturn(Optional.of(visit));

        service.cancelVisit(visitUuid, "Owner cancelled");

        assertEquals(VisitStatus.CANCELLED, visit.getStatus());
        verify(visitNotificationService).notifyCancelVisitParticipants(
                visit, pet, vet, "Owner cancelled");
    }

    /**
     * Verifies that completing an in-progress visit changes its status to COMPLETED
     * and delegates creation of the associated medical record to the medical service.
     */
    @Test
    void completeVisit_shouldCompleteVisitAndCreateMedicalRecord() {
        LocalDateTime now = LocalDateTime.now();
        Visit visit = new Visit();
        visit.setVet(vet);
        visit.setPet(pet);
        visit.setRoom(room);
        visit.setVisitType(VisitType.ONSITE);
        visit.setTimeRange(new DateTimeRange(now.minusMinutes(5), now.plusMinutes(5)));
        visit.setStatus(VisitStatus.SCHEDULED);
        visit.setDescription("Visit");
        visit.setUuid(visitUuid);

        CompleteVisitRequestDto request = new CompleteVisitRequestDto(
                "Ear infection", "Inflammation observed", null,
                "Medication for seven days", "Keep the ear clean", true,
                LocalDate.now().plusDays(14), null, null);

        when(visitRepository.findByUuidForUpdate(visitUuid)).thenReturn(Optional.of(visit));

        service.completeVisit(visitUuid, request);

        assertEquals(VisitStatus.COMPLETED, visit.getStatus());
        ArgumentCaptor<MedicalRecord> recordCaptor = ArgumentCaptor.forClass(MedicalRecord.class);
        verify(medicalRecordService).create(recordCaptor.capture(), eq(request), eq(visit));
        assertNotNull(recordCaptor.getValue());
    }

    @Test
    void bookVisit_shouldNotSaveWhenVetHasNoAvailability() {
        LocalDate date = LocalDate.now().plusDays(1);
        CreateVisitRequestDto request = new CreateVisitRequestDto(
                petUuid, vetUuid, room.getUuid(), date, LocalTime.of(10, 0),
                VisitType.ONSITE, 15, "Checkup");
        when(vetService.getVetWithUuidLock(vetUuid)).thenReturn(vet);
        when(vetAvailabilityService.findAvailableByUuidAndTimeRange(any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ConflictException.class, () -> service.bookVisit(request));

        verify(visitRepository, never()).save(any(Visit.class));
        verifyNoInteractions(visitNotificationService);
    }

    @Test
    void bookVisit_shouldNotSaveWhenClinicIsClosed() {
        LocalDate date = LocalDate.now().plusDays(1);
        CreateVisitRequestDto request = new CreateVisitRequestDto(
                petUuid, vetUuid, room.getUuid(), date, LocalTime.of(7, 0),
                VisitType.ONSITE, 15, "Checkup");
        when(vetService.getVetWithUuidLock(vetUuid)).thenReturn(vet);

        assertThrows(BadRequestException.class, () -> service.bookVisit(request));

        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void bookVisit_shouldRoundDurationUpToAvailabilityBlock() {
        LocalDate date = LocalDate.now().plusDays(1);
        CreateVisitRequestDto request = new CreateVisitRequestDto(
                petUuid, vetUuid, room.getUuid(), date, LocalTime.of(10, 0),
                VisitType.ONSITE, 16, "Checkup");
        when(vetService.getVetWithUuidLock(vetUuid)).thenReturn(vet);
        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.bookVisit(request);

        ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(visitCaptor.capture());
        assertEquals(LocalDateTime.of(date, LocalTime.of(10, 20)), visitCaptor.getValue().getEndTime());
    }

    @Test
    void bookVisit_shouldRequireRoomForOnsiteVisit() {
        CreateVisitRequestDto request = new CreateVisitRequestDto(
                petUuid, vetUuid, null, LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                VisitType.ONSITE, 15, "Checkup");

        assertThrows(BadRequestException.class, () -> service.bookVisit(request));
        verifyNoInteractions(visitRepository);
    }

    @Test
    void bookVisit_shouldRejectRoomForOnlineVisit() {
        CreateVisitRequestDto request = new CreateVisitRequestDto(
                petUuid, vetUuid, room.getUuid(), LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                VisitType.ONLINE, 15, "Checkup");

        assertThrows(BadRequestException.class, () -> service.bookVisit(request));
        verifyNoInteractions(visitRepository);
    }

    @Test
    void bookVisit_shouldRejectPetConflictEvenWhenOtherResourcesDiffer() {
        LocalDate date = LocalDate.now().plusDays(1);
        CreateVisitRequestDto request = new CreateVisitRequestDto(
                petUuid, vetUuid, room.getUuid(), date, LocalTime.of(10, 0),
                VisitType.ONSITE, 15, "Checkup");
        when(vetService.getVetWithUuidLock(vetUuid)).thenReturn(vet);
        when(visitRepository.existsPetReservation(
                petUuid, LocalDateTime.of(date, LocalTime.of(10, 0)),
                LocalDateTime.of(date, LocalTime.of(10, 15)), null)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.bookVisit(request));
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void bookVisit_shouldRejectRoomConflictWithAnotherVet() {
        LocalDate date = LocalDate.now().plusDays(1);
        CreateVisitRequestDto request = new CreateVisitRequestDto(
                petUuid, vetUuid, room.getUuid(), date, LocalTime.of(10, 0),
                VisitType.ONSITE, 15, "Checkup");
        when(vetService.getVetWithUuidLock(vetUuid)).thenReturn(vet);
        when(visitRepository.existsRoomReservation(
                room.getUuid(), LocalDateTime.of(date, LocalTime.of(10, 0)),
                LocalDateTime.of(date, LocalTime.of(10, 15)), null)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.bookVisit(request));
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void cancelVisit_shouldBeIdempotentForAlreadyCancelledVisit() {
        Visit visit = new Visit().schedule(
                vet, pet, room,
                LocalDate.now().plusDays(1).atTime(9, 0),
                LocalDate.now().plusDays(1).atTime(9, 20),
                VisitType.ONSITE, "Visit");
        visit.setStatus(VisitStatus.CANCELLED);
        when(visitRepository.findByUuid(visitUuid)).thenReturn(Optional.of(visit));

        service.cancelVisit(visitUuid, "duplicate request");

        verifyNoInteractions(visitNotificationService);
    }

    @Test
    void cancelVisit_shouldRejectCompletedVisitAsConflict() {
        Visit visit = new Visit().schedule(
                vet, pet, room,
                LocalDate.now().plusDays(1).atTime(9, 0),
                LocalDate.now().plusDays(1).atTime(9, 20),
                VisitType.ONSITE, "Visit");
        visit.setStatus(VisitStatus.COMPLETED);
        when(visitRepository.findByUuid(visitUuid)).thenReturn(Optional.of(visit));

        assertThrows(ConflictException.class, () -> service.cancelVisit(visitUuid, "late cancel"));

        verifyNoInteractions(visitNotificationService);
    }

    @Test
    void completeVisit_shouldRejectVisitThatHasNotStarted() {
        Visit visit = new Visit().schedule(
                vet, pet, room,
                LocalDate.now().plusDays(1).atTime(9, 0),
                LocalDate.now().plusDays(1).atTime(9, 20),
                VisitType.ONSITE, "Visit");
        when(visitRepository.findByUuidForUpdate(visitUuid)).thenReturn(Optional.of(visit));

        assertThrows(BadRequestException.class, () -> service.completeVisit(visitUuid, null));

        verifyNoInteractions(medicalRecordService);
    }

    @Test
    void cancelVisit_shouldReportMissingVisit() {
        when(visitRepository.findByUuid(visitUuid)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.cancelVisit(visitUuid, "reason"));
    }
}
