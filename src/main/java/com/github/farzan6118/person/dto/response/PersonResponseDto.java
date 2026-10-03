package com.github.farzan6118.person.dto.response;

import java.time.LocalDate;

public record PersonResponseDto(
        String title,
        String firstName,
        String lastName,
        String nationalId,
        LocalDate birthDate,
        String photo
) {
}
