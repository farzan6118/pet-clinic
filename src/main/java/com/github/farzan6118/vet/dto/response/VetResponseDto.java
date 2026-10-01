package com.github.farzan6118.vet.dto.response;

import com.github.farzan6118.person.dto.response.AddressResponseDto;
import com.github.farzan6118.person.dto.response.PersonResponseDto;
import com.github.farzan6118.person.dto.response.ProfileResponseDto;

import java.util.UUID;

public record VetResponseDto(
        UUID uuid,
        PersonResponseDto person,
        ProfileResponseDto profile,
        AddressResponseDto address,
        UUID clinicUuid
) {
}
