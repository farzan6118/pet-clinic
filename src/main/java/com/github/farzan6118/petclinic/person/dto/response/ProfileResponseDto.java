package com.github.farzan6118.petclinic.person.dto.response;

import java.time.LocalDate;

public record ProfileResponseDto(
        String email,
        String mobileNumber,
        LocalDate birthDate,
        String photo
) {
}
