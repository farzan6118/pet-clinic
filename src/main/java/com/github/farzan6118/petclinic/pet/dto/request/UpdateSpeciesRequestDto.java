package com.github.farzan6118.petclinic.pet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSpeciesRequestDto(
        @NotBlank(message = "pet.type.is.required")
        @Schema(example = "cat")
        String name,
        @NotBlank(message = "species.code.is.required") @Size(max = 50)
        @Schema(example = "100-001")
        String code,
        @Schema(example = "Iran")
        String origin,
        @Schema(example = "Persian cat")
        String description
) {
}
