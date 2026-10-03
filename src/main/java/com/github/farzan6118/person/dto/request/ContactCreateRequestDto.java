package com.github.farzan6118.person.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactCreateRequestDto(
        @Email
        @NotBlank
        @Schema(example = "user-one@test.com")
        String email,

        @NotBlank
        @Size(max = 20)
        @Schema(example = "09123456789")
        String mobileNumber,

        String socialMedia
) {
}
