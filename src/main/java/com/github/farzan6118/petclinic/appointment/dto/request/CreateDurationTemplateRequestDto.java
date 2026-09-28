package com.github.farzan6118.petclinic.appointment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateDurationTemplateRequestDto(
        @Schema(example = "QUICK")
        @NotBlank(message = "name is required")
        String name,
        @NotNull(message = "duration is required")
        @PositiveOrZero(message = "invalid duration")
        Integer durationMinutes,
        @Schema(example = "Quick visit")
        String description

) {
}
