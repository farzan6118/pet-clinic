package com.github.farzan6118.appointment.service;

import com.github.farzan6118.appointment.dto.request.CompleteVisitRequestDto;
import com.github.farzan6118.appointment.dto.request.CreateVisitRequestDto;
import com.github.farzan6118.appointment.dto.request.RescheduleVisitRequestDto;
import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.appointment.repository.VisitRepository;
import com.github.farzan6118.clinic.model.Room;
import com.github.farzan6118.clinic.service.RoomService;
import com.github.farzan6118.common.enums.VisitStatus;
import com.github.farzan6118.common.enums.VisitType;
import com.github.farzan6118.common.exception.BadRequestException;
import com.github.farzan6118.common.exception.ConflictException;
import com.github.farzan6118.common.exception.ResourceNotFoundException;
import com.github.farzan6118.config.ClinicProperties;
import com.github.farzan6118.infrastructure.email.VisitNotificationService;
import com.github.farzan6118.pet.model.MedicalRecord;
import com.github.farzan6118.pet.model.Pet;
import com.github.farzan6118.pet.service.MedicalRecordService;
import com.github.farzan6118.pet.service.PetService;
import com.github.farzan6118.vet.model.Vet;
import com.github.farzan6118.vet.service.VetAvailabilityService;
import com.github.farzan6118.vet.service.VetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VisitServiceCommandImpl implements VisitServiceCommand {

    private final ClinicProperties clinicProperties;
    private final VisitRepository visitRepository;
    private final RoomService roomService;
    private final PetService petService;
    private final VetService vetService;
    private final VetAvailabilityService vetAvailabilityService;
    private final VisitNotificationService visitNotificationService;
    private final MedicalRecordService medicalRecordService;

    /**
     * Book an available appointment slot for a pet.
     */
    @Override
    public void bookVisit(CreateVisitRequestDto request) {
        Room room = resolveRoom(request.visitType(), request.roomUuid());
        int requestedDurationMinutes = request.durationMinutes() != null ? request.durationMinutes() : 15;

        LocalDateTime visitStart = LocalDateTime.of(request.visitDate(), request.visitTime());
        LocalDateTime visitEnd = getVisitEnd(visitStart, requestedDurationMinutes);

        dateAndTimeValidations(visitStart, visitEnd);

        Vet vet = vetService.getVetWithUuidLock(request.vetUuid());
        Pet pet = petService.getEntityByUuidForUpdate(request.petUuid());

        Visit visit = new Visit()
                .schedule(vet, pet, room, visitStart, visitEnd, request.visitType(), request.description());

        validateVisitTime(visitStart, visitEnd);
        vetAvailabilityValidation(vet, visitStart, visitEnd, null);
        validateRoomAvailability(room, visitStart, visitEnd, null);
        petAvailabilityValidation(pet, visitStart, visitEnd, null);

        Visit savedVisit = visitRepository.save(visit);

        visitNotificationService.notifyBookVisitParticipants(savedVisit, pet, vet);

        log.info("Visit booked successfully. visitUuid={}, petUuid={}, vetUuid={}, roomUuid={}, startTime={}, endTime={}",
                savedVisit.getUuid(),
                pet.getUuid(),
                vet.getUuid(),
                room != null ? room.getUuid() : null, visitStart,
                visitEnd
        );
    }

    private Room resolveRoom(VisitType visitType, UUID roomUuid) {
        if (visitType == VisitType.ONSITE && roomUuid == null) {
            throw new BadRequestException("room.uuid.is.required.for.onsite.visit");
        }
        if (visitType != VisitType.ONSITE && roomUuid != null) {
            throw new BadRequestException("room.uuid.must.be.empty.for.remote.visit");
        }
        if (roomUuid == null) {
            return null;
        }

        Room room = roomService.getEntityByUuidForUpdate(roomUuid);
        if (!room.isActive() || room.getBuilding() == null || !room.getBuilding().isActive()) {
            throw new ConflictException("The selected room is not available");
        }
        return room;
    }

    private void vetAvailabilityValidation(Vet vet, LocalDateTime visitStart, LocalDateTime visitEnd, UUID visitUuid) {
        if (vet == null) {
            return;
        }

        vetAvailabilityService.findAvailableByUuidAndTimeRange(vet.getUuid(), visitStart, visitEnd)
                .orElseThrow(() -> new ConflictException(
                        "The veterinarian is not available at this time",
                        "No veterinarian availability covers the requested time")
                );

        boolean existsVetReservation = visitRepository.existsVetReservation(
                vet.getUuid(), visitStart, visitEnd, visitUuid);

        if (existsVetReservation) {
            throw new ConflictException(
                    "The veterinarian already has a visit at this time",
                    "A veterinarian visit overlaps the requested time");
        }
    }

    private void validateVisitTime(LocalDateTime visitStart, LocalDateTime visitEnd) {
        LocalDate visitDate = visitStart.toLocalDate();

        if (clinicProperties.closeDays().contains(visitDate.getDayOfWeek())) {
            String message = String.format("the clinic is closed on %s (%s)", visitDate.getDayOfWeek(), visitDate);
            throw new BadRequestException(message, message);
        }

        ClinicProperties.WorkingHours workingHours = clinicProperties.workingHours();

        if (visitStart.toLocalTime().isBefore(workingHours.start()) ||
                visitEnd.toLocalTime().isAfter(workingHours.end())) {

            String message = String.format("the visit must be scheduled between %s and %s on %s",
                    workingHours.start(), workingHours.end(), visitDate);

            throw new BadRequestException(message, message);
        }
    }

    private void validateRoomAvailability(Room room, LocalDateTime visitStart, LocalDateTime visitEnd, UUID visitUuid) {
        if (room == null) {
            return;
        }

        boolean existsRoomReservation = visitRepository.existsRoomReservation(
                room.getUuid(), visitStart, visitEnd, visitUuid);

        if (existsRoomReservation) {
            String message = String.format("the room %s is already booked from %s to %s",
                    room.getName(), visitStart, visitEnd);

            throw new ConflictException(message, message);
        }
    }

    private void petAvailabilityValidation(Pet pet, LocalDateTime visitStart, LocalDateTime visitEnd, UUID visitUuid) {
        boolean existsPetReservation = visitRepository.existsPetReservation(
                pet.getUuid(), visitStart, visitEnd, visitUuid);

        if (existsPetReservation) {
            String message = String.format("the pet already has an appointment from %s to %s",
                    visitStart, visitEnd);

            throw new ConflictException(message, message);
        }
    }

    private void dateAndTimeValidations(LocalDateTime startTime, LocalDateTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException(
                    "Visit end time must be after its start time",
                    "Invalid visit time range: end is not after start");
        }

        if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
            throw new BadRequestException(
                    "Visit start and end must be on the same day",
                    "Invalid visit time range spans multiple days");
        }
    }

    @Override
    public void cancelVisit(UUID uuid, String reason) {

        Visit visit = getVisitByUuid(uuid);

        if (visit.getStatus() == VisitStatus.CANCELLED) {
            return;
        }

        if (visit.getStatus() == VisitStatus.COMPLETED) {
            throw new ConflictException("A completed visit cannot be cancelled", "Completed visit cannot be cancelled");
        }

        visit.cancel();

        visitNotificationService.notifyCancelVisitParticipants(visit, visit.getPet(), visit.getVet(), reason);

        log.info("Visit cancelled. visitUuid={}", visit.getUuid());
    }

    /**
     * Complete a visit.
     */
    @Override
    public void completeVisit(UUID uuid, CompleteVisitRequestDto request) {
        Visit visit = getVisitForUpdate(uuid);
        validateCompletion(visit);
        visit.complete(LocalDateTime.now());

        if (request != null) {
            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecordService.create(medicalRecord, request, visit);
        }

        log.info("Visit completed successfully. visitUuid={}", uuid);
    }

    private Visit getVisitForUpdate(UUID uuid) {
        return visitRepository.findByUuidForUpdate(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found", "Visit not found: " + uuid));
    }

    private void validateCompletion(Visit visit) {
        validateStatus(visit);
//        validateScheduledTime(visit);
    }

    private void validateStatus(Visit visit) {

        switch (visit.getStatus()) {

            case CANCELLED ->
                    throw new ConflictException("A cancelled visit cannot be completed", "Cancelled visit cannot be completed");

            case COMPLETED -> throw new ConflictException("Visit is already completed", "Visit is already completed");

            default -> {
                // Valid states can continue.
            }
        }
    }

    private void validateScheduledTime(Visit visit) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startTime = visit.getStartTime();
        LocalDateTime endTime = visit.getEndTime();

        if (now.isBefore(startTime)) {
            throw new BadRequestException("Visit has not started yet", "Completion attempted before scheduled start");
        }

        if (now.isAfter(endTime)) {
            throw new BadRequestException("Visit has already finished", "Completion attempted after scheduled end");
        }
    }

    /**
     * Reschedule a visit to another available slot.
     */
    @Override
    public void rescheduleVisit(UUID uuid, RescheduleVisitRequestDto request) {
        Visit visit = visitRepository.findByUuidForUpdate(uuid).orElseThrow(
                () -> new ResourceNotFoundException("Visit not found", "Visit not found: " + uuid));

        VisitType visitType = request.visitType() != null ? request.visitType() : visit.getVisitType();
        Room newRoom = resolveRoom(visitType, request.roomUuid());
        int existingDurationMinutes = Math.toIntExact(
                Duration.between(visit.getStartTime(), visit.getEndTime()).toMinutes());
        LocalDateTime newVisitStart = LocalDateTime.of(request.visitDate(), request.visitTime());
        LocalDateTime newVisitEnd = getVisitEnd(newVisitStart, existingDurationMinutes);

        dateAndTimeValidations(newVisitStart, newVisitEnd);
        Vet vet = vetService.getVetWithUuidLock(visit.getVet().getUuid());
        Pet pet = petService.getEntityByUuidForUpdate(visit.getPet().getUuid());

        LocalDateTime oldVisitStart = visit.getStartTime();

        validateVisitTime(newVisitStart, newVisitEnd);
        vetAvailabilityValidation(vet, newVisitStart, newVisitEnd, visit.getUuid());
        validateRoomAvailability(newRoom, newVisitStart, newVisitEnd, visit.getUuid());
        petAvailabilityValidation(pet, newVisitStart, newVisitEnd, visit.getUuid());

        visit.reschedule(newRoom, newVisitStart, newVisitEnd, visitType, request.description());
        visitNotificationService.notifyRescheduleVisitParticipants(oldVisitStart, visit, pet, vet, newVisitStart);
        visitRepository.save(visit);
        log.info("Visit rescheduled successfully. visitUuid={}, oldVisitStart={}, newVisitStart={}",
                visit.getUuid(), oldVisitStart, newVisitStart);
    }

    private Visit getVisitByUuid(UUID uuid) {
        return visitRepository.findByUuid(uuid).orElseThrow(
                () -> new ResourceNotFoundException("Visit not found", "Visit not found: " + uuid));
    }

    private LocalDateTime getVisitEnd(LocalDateTime visitStart, int requestedMinutes) {
        int blockMinutes = clinicProperties.timeBlockMinutes();
        if (blockMinutes <= 0) {
            throw new BadRequestException("Availability time block must be positive");
        }
        int roundedMinutes = Math.multiplyExact(
                (requestedMinutes - 1) / blockMinutes + 1, blockMinutes);
        return visitStart.plusMinutes(roundedMinutes);
    }

}
