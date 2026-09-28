package com.github.farzan6118.petclinic.owner.dto.response;

import com.github.farzan6118.petclinic.person.dto.response.AddressResponseDto;
import com.github.farzan6118.petclinic.person.dto.response.PersonResponseDto;
import com.github.farzan6118.petclinic.person.dto.response.ProfileResponseDto;

import java.util.UUID;

public record OwnerResponseDto(
        UUID uuid,
        PersonResponseDto person,
        ProfileResponseDto profile,
        AddressResponseDto address
) {
}
