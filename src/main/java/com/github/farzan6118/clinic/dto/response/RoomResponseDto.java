package com.github.farzan6118.clinic.dto.response;

import java.util.UUID;

public record RoomResponseDto(
        UUID uuid,
        String name,
        String roomNumber,
        RoomTypeResponseDto roomType,
        Boolean active,
        UUID clinicUuid
) {
}
