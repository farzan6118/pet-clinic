package com.github.farzan6118.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateRoomRequestDto(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 20) String roomNumber,
        @NotNull UUID roomTypeUuid,
        @NotNull UUID clinicUuid,
        @NotNull Boolean active
) {
}
