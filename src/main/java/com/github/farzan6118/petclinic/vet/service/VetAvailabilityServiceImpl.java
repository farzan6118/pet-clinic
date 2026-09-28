package com.github.farzan6118.petclinic.vet.service;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import com.github.farzan6118.petclinic.common.exception.BadRequestException;
import com.github.farzan6118.petclinic.common.exception.ConflictException;
import com.github.farzan6118.petclinic.common.exception.ResourceNotFoundException;
import com.github.farzan6118.petclinic.common.mapper.PageMapper;
import com.github.farzan6118.petclinic.vet.dto.request.VetAvailabilityCreateRequestDto;
import com.github.farzan6118.petclinic.vet.dto.request.VetAvailabilityUpdateRequestDto;
import com.github.farzan6118.petclinic.vet.dto.response.VetAvailabilityResponseDto;
import com.github.farzan6118.petclinic.vet.mapper.VetAvailabilityMapper;
import com.github.farzan6118.petclinic.vet.model.Vet;
import com.github.farzan6118.petclinic.vet.model.VetAvailability;
import com.github.farzan6118.petclinic.vet.repository.VetAvailabilityRepository;
import com.github.farzan6118.petclinic.vet.repository.VetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VetAvailabilityServiceImpl implements VetAvailabilityService {

    private final VetAvailabilityRepository availabilityRepository;
    private final VetRepository vetRepository;
    private final PageMapper pageMapper;
    private final VetAvailabilityMapper vetAvailabilityMapper;

    @Override
    @Transactional
    public void createAvailability(VetAvailabilityCreateRequestDto request) {
        LocalDateTime startDateTime = request.startTime();
        LocalDateTime endDateTime = request.endTime();
        Vet vet = getVet(request.vetUuid());
        validateTimeRange(startDateTime, endDateTime);
        checkCreateOverlapping(request.vetUuid(), startDateTime, endDateTime);
        VetAvailability availability = VetAvailability.create(vet, startDateTime, endDateTime);

        availabilityRepository.save(availability);
    }

    private void checkCreateOverlapping(UUID vetUuid, LocalDateTime startTime, LocalDateTime endTime) {
        boolean overlapping = availabilityRepository.existsOverlappingAvailability(vetUuid, startTime, endTime);
        if (overlapping) {
            throw new ConflictException("This availability overlaps another veterinarian availability", "Vet already has an availability overlapping this time range");
        }
    }

    @Override
    @Transactional
    public void updateAvailability(UUID availabilityUuid, VetAvailabilityUpdateRequestDto request) {
        VetAvailability availability = availabilityRepository.findByUuidAndVetUuid(availabilityUuid, request.vetUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Vet availability not found"));
        LocalDateTime startDateTime = request.startTime();
        LocalDateTime endDateTime = request.endTime();
        validateTimeRange(startDateTime, endDateTime);
        checkUpdateOverlapping(request.vetUuid(), availabilityUuid, startDateTime, endDateTime);
        availability.update(startDateTime, endDateTime);
    }

    private void checkUpdateOverlapping(UUID vetUuid, UUID availabilityUuid,
                                        LocalDateTime startTime, LocalDateTime endTime) {
        boolean overlapping = availabilityRepository.existsOverlappingAvailabilityForUpdate(
                vetUuid, availabilityUuid, startTime, endTime);
        if (overlapping) {
            throw new ConflictException("This availability overlaps another veterinarian availability", "Vet already has an availability overlapping this time range");
        }
    }

    @Override
    @Transactional
    public void deleteAvailability(UUID vetUuid, UUID availabilityUuid) {

        VetAvailability availability = availabilityRepository.findByUuidAndVetUuid(availabilityUuid, vetUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Vet availability not found"));
        if (availability.getEntityStatus() != EntityStatus.ACTIVE) {
            throw new ConflictException("Veterinarian availability is already inactive", "vet availability is already inactive");
        }
        availability.setEntityStatus(EntityStatus.DELETED);
        availability.setActive(false);
    }

    @Override
    public PageResponseDto<VetAvailabilityResponseDto> getVetAvailabilityPageable(
            UUID vetUuid, PageAndSortRequestDto pageRequest) {
        Pageable pageable = pageMapper.getPageable(pageRequest);
        Page<VetAvailability> vetAvailabilityPage = availabilityRepository.findAllByVetUuid(vetUuid, pageable);
        return pageMapper.toPageResponse(vetAvailabilityPage, vetAvailabilityMapper::mapToDto);

    }

    private Vet getVet(UUID vetUuid) {
        return vetRepository.findByUuid(vetUuid).orElseThrow(() -> new ResourceNotFoundException("Vet not found"));
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("Availability start time must be before its end time");
        }
    }

    @Override
    public Optional<Vet> findAvailableByUuidAndTimeRange(
            UUID uuid, LocalDateTime StartTime, LocalDateTime EndTime) {
        return vetRepository.findAvailableByUuidAndTimeRange(uuid, StartTime, EndTime);
    }

    @Override
    public List<VetAvailabilityResponseDto> getByDate(LocalDate localDate) {
        LocalDateTime startDateTime = localDate.atStartOfDay();
        LocalDateTime endDateTime = localDate.atStartOfDay().plusDays(1);
        List<VetAvailability> vetAvailabilities = availabilityRepository.findAllByTimeRange(
                startDateTime, endDateTime);
        return vetAvailabilities.stream().map(vetAvailabilityMapper::mapToDto).toList();
    }

}
