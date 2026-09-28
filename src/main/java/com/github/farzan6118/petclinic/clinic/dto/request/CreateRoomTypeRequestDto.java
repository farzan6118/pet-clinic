package com.github.farzan6118.petclinic.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoomTypeRequestDto(
        @NotBlank @Size(max = 255) String name,
        String description
) {
}
