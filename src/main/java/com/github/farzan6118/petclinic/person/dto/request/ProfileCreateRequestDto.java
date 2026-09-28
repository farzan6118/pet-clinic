package com.github.farzan6118.petclinic.person.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProfileCreateRequestDto(
        @Email
        @NotBlank
        @Schema(example = "user-one@test.com")
        String email,

        @NotBlank
        @Size(max = 20)
        @Schema(example = "09123456789")
        String mobileNumber,

        @Past
        @Schema(example = "1980-10-10")
        LocalDate birthDate,

        String photo
) {
}
