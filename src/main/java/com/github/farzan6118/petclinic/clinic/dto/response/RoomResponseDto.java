package com.github.farzan6118.petclinic.clinic.dto.response;

import java.util.UUID;

public record RoomResponseDto(
        UUID uuid,
        String name,
        String code,
        RoomTypeResponseDto roomType,
        Boolean active,
        UUID clinicUuid
) {
}
