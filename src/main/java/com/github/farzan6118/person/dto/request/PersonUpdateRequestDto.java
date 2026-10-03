package com.github.farzan6118.person.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PersonUpdateRequestDto(
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
        String nationalId,

        @Past
        @Schema(example = "2000-10-10")
        LocalDate birthDate,

        String photo
) {
}
