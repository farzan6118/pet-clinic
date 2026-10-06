package com.github.farzan6118.vet.service;

import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.vet.dto.request.VetAvailabilityCreateRequestDto;
import com.github.farzan6118.vet.dto.request.VetAvailabilityUpdateRequestDto;
import com.github.farzan6118.vet.dto.response.VetAvailabilityResponseDto;
import com.github.farzan6118.vet.model.Vet;
import com.github.farzan6118.vet.model.VetAvailability;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VetAvailabilityService {

    void createAvailability(VetAvailabilityCreateRequestDto request);

    void updateAvailability(UUID availabilityUuid, VetAvailabilityUpdateRequestDto request);

    void deleteAvailability(UUID vetUuid, UUID availabilityUuid);

    PageResponseDto<VetAvailabilityResponseDto> getVetAvailabilityPageable(
            UUID vetUuid, PageAndSortRequestDto pageRequest);

    Optional<Vet> findAvailableByUuidAndTimeRange(UUID uuid, LocalDateTime StartTime, LocalDateTime EndTime);

    List<VetAvailabilityResponseDto> getByDate(LocalDate localDate);

    List<VetAvailability> findActiveByVetUuidAndOverlappingDay(UUID vetUuid, LocalDate date);
}
