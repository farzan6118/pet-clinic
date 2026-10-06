package com.github.farzan6118.pet.dto.request;

import com.github.farzan6118.common.enums.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePetRequestDto(
        @Schema(example = "Pet")
        @NotBlank(message = "pet.name.is.required")
        String name,
        @Schema(example = "black and white")
        String color,
        @Schema(example = "blue eyes")
        String marks,
        @NotNull(message = "pet.sex.is.required")
        Sex sex,
        @Past(message = "invalid.birth.date")
        @Schema(example = "2024-08-29")
        LocalDate birthDate,
        @NotNull(message = "species.is.required")
        UUID speciesUuid,
        @NotNull(message = "owner.is.required")
        UUID ownerUuid
) {
}
