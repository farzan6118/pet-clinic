package com.github.farzan6118.petclinic.vet.service;

import com.github.farzan6118.petclinic.common.exception.BadRequestException;
import com.github.farzan6118.petclinic.common.exception.ConflictException;
import com.github.farzan6118.petclinic.common.exception.ResourceNotFoundException;
import com.github.farzan6118.petclinic.common.mapper.PageMapper;
import com.github.farzan6118.petclinic.vet.dto.request.VetAvailabilityCreateRequestDto;
import com.github.farzan6118.petclinic.vet.dto.request.VetAvailabilityUpdateRequestDto;
import com.github.farzan6118.petclinic.vet.mapper.VetAvailabilityMapper;
import com.github.farzan6118.petclinic.vet.model.Vet;
import com.github.farzan6118.petclinic.vet.model.VetAvailability;
import com.github.farzan6118.petclinic.vet.repository.VetAvailabilityRepository;
import com.github.farzan6118.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetAvailabilityServiceImplTest {

    private final UUID vetUuid = UUID.randomUUID();
    private final UUID availabilityUuid = UUID.randomUUID();
    private final LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(9).withMinute(0).withSecond(0).withNano(0);
    private final LocalDateTime end = start.plusHours(2);
    @Mock
    private VetAvailabilityRepository availabilityRepository;
    @Mock
    private VetRepository vetRepository;
    @Mock
    private PageMapper pageMapper;
    @Mock
    private VetAvailabilityMapper vetAvailabilityMapper;
    @InjectMocks
    private VetAvailabilityServiceImpl service;

    @Test
    void createAvailability_savesForActiveVetWhenSlotDoesNotOverlap() {
        Vet vet = new Vet();
        when(vetRepository.findByUuid(vetUuid)).thenReturn(Optional.of(vet));
        when(availabilityRepository.existsOverlappingAvailability(vetUuid, start, end)).thenReturn(false);

        service.createAvailability(new VetAvailabilityCreateRequestDto(vetUuid, start, end));

        verify(availabilityRepository).save(argThat(a -> a.getVet() == vet
                && a.getTimeRange().getStartDateTime().equals(start)
                && a.getTimeRange().getEndDateTime().equals(end)));
    }

    @Test
    void createAvailability_rejectsOverlap() {
        when(vetRepository.findByUuid(vetUuid)).thenReturn(Optional.of(new Vet()));
        when(availabilityRepository.existsOverlappingAvailability(vetUuid, start, end)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createAvailability(
                new VetAvailabilityCreateRequestDto(vetUuid, start, end)));
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void createAvailability_rejectsInvalidRange() {
        when(vetRepository.findByUuid(vetUuid)).thenReturn(Optional.of(new Vet()));

        assertThrows(BadRequestException.class, () -> service.createAvailability(
                new VetAvailabilityCreateRequestDto(vetUuid, end, start)));
        verify(availabilityRepository, never()).save(any());
        verify(availabilityRepository, never()).existsOverlappingAvailability(any(), any(), any());
    }

    @Test
    void updateAvailability_checksConflictsExcludingCurrentSlot() {
        VetAvailability availability = new VetAvailability();
        when(availabilityRepository.findByUuidAndVetUuid(availabilityUuid, vetUuid)).thenReturn(Optional.of(availability));
        when(availabilityRepository.existsOverlappingAvailabilityForUpdate(vetUuid, availabilityUuid, start, end))
                .thenReturn(false);

        service.updateAvailability(availabilityUuid, new VetAvailabilityUpdateRequestDto(vetUuid, start, end));

        verify(availabilityRepository).existsOverlappingAvailabilityForUpdate(vetUuid, availabilityUuid, start, end);
        assertEquals(start, availability.getTimeRange().getStartDateTime());
        assertEquals(end, availability.getTimeRange().getEndDateTime());
    }

    @Test
    void updateAvailability_rejectsOverlap() {
        when(availabilityRepository.findByUuidAndVetUuid(availabilityUuid, vetUuid))
                .thenReturn(Optional.of(new VetAvailability()));
        when(availabilityRepository.existsOverlappingAvailabilityForUpdate(vetUuid, availabilityUuid, start, end))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> service.updateAvailability(availabilityUuid,
                new VetAvailabilityUpdateRequestDto(vetUuid, start, end)));
    }

    @Test
    void deleteAvailability_softDeletes() {
        VetAvailability availability = new VetAvailability();
        availability.setUuid(availabilityUuid);
        when(availabilityRepository.findByUuidAndVetUuid(availabilityUuid, vetUuid)).thenReturn(Optional.of(availability));

        service.deleteAvailability(vetUuid, availabilityUuid);

        assertFalse(availability.isActive());
        assertEquals(com.github.farzan6118.petclinic.common.enums.EntityStatus.DELETED,
                availability.getEntityStatus());
        verify(availabilityRepository, never()).delete(availability);
    }

    @Test
    void updateAvailability_reportsMissingRecord() {
        when(availabilityRepository.findByUuidAndVetUuid(availabilityUuid, vetUuid)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateAvailability(availabilityUuid,
                new VetAvailabilityUpdateRequestDto(vetUuid, start, end)));
    }
}
