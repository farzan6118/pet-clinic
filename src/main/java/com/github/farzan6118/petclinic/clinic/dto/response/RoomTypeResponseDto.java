package com.github.farzan6118.petclinic.clinic.dto.response;

import com.github.farzan6118.petclinic.common.enums.EntityStatus;

import java.util.UUID;

public record RoomTypeResponseDto(
        UUID uuid,
        String name,
        String description,
        EntityStatus status
) {
}
