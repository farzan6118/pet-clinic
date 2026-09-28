package com.github.farzan6118.petclinic.person.dto.response;

public record PersonResponseDto(
        String title,
        String firstName,
        String lastName,
        String nationalId
) {
}
