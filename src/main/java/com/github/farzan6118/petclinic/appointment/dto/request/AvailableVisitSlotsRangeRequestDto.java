package com.github.farzan6118.petclinic.appointment.dto.request;

import com.github.farzan6118.petclinic.common.enums.VisitType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record AvailableVisitSlotsRangeRequestDto(
        @NotNull UUID vetUuid,
        @NotNull UUID petUuid,
        @NotNull @FutureOrPresent LocalDate dateFrom,
        @NotNull @FutureOrPresent LocalDate dateTo,
        @NotNull VisitType visitType,
        @NotNull @Min(2) @Max(120) Integer durationMinutes,
        @NotNull @Min(1) @Max(60) Integer intervalMinutes
) {
}
