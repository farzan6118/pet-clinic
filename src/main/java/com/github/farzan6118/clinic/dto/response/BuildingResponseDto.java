package com.github.farzan6118.clinic.dto.response;

import com.github.farzan6118.person.dto.response.AddressResponseDto;

import java.util.UUID;

public record BuildingResponseDto(
        UUID uuid,
        String name,
        Integer code,
        AddressResponseDto address,
        boolean active
) {
}
