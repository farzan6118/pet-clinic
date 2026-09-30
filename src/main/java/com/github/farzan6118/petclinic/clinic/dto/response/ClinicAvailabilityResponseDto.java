package com.github.farzan6118.petclinic.clinic.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ClinicAvailabilityResponseDto(
        DayOfWeek dayOfWeek,
        boolean available,
        LocalTime openingTime,
        LocalTime closingTime
) {
}
