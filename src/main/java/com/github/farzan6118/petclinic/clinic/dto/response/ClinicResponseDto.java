package com.github.farzan6118.petclinic.clinic.dto.response;

import com.github.farzan6118.petclinic.person.dto.response.AddressResponseDto;

import java.util.UUID;

public record ClinicResponseDto(
        UUID uuid,
        String name,
        Integer code,
        AddressResponseDto address,
        boolean active
) {
}
