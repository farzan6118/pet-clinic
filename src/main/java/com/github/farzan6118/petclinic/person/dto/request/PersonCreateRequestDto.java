package com.github.farzan6118.petclinic.person.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record PersonCreateRequestDto(
        @Size(max = 10)
        @Schema(example = "Dr.")
        String title,

        @Size(max = 128)
        @Schema(example = "John")
        String firstName,

        @Size(max = 128)
        @Schema(example = "Doe")
        String lastName,

        @Size(max = 20)
        @Schema(example = "1234567891")
        String nationalId
) {
}