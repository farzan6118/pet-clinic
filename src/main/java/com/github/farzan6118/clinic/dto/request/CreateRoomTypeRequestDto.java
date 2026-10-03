package com.github.farzan6118.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoomTypeRequestDto(
        @NotBlank @Size(max = 255) String name,
        String description
) {
}
