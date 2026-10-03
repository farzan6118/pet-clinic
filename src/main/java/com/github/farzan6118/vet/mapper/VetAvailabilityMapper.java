package com.github.farzan6118.vet.mapper;

import com.github.farzan6118.vet.dto.response.VetAvailabilityResponseDto;
import com.github.farzan6118.vet.model.VetAvailability;
import org.springframework.stereotype.Component;

@Component
public class VetAvailabilityMapper {

    public VetAvailabilityResponseDto mapToDto(VetAvailability vetAvailability) {
        return new VetAvailabilityResponseDto(
                vetAvailability.getUuid(),
                vetAvailability.getVet().getUuid(),
                vetAvailability.getVet().getFullName(),
                vetAvailability.getTimeRange().getStartDate(),
                vetAvailability.getTimeRange().getStartTime(),
                vetAvailability.getTimeRange().getEndTime(),
                vetAvailability.isActive());
    }
}
