package com.github.farzan6118.petclinic.vet.mapper;

import com.github.farzan6118.petclinic.vet.dto.response.VetAvailabilityResponseDto;
import com.github.farzan6118.petclinic.vet.model.VetAvailability;
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
