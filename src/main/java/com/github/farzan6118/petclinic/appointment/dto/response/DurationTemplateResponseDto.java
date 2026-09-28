package com.github.farzan6118.petclinic.appointment.dto.response;

import java.util.UUID;

public record DurationTemplateResponseDto(
        UUID uuid,
        String name,
        Integer durationMinutes,
        String description
) {
}
