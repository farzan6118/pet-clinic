package com.github.farzan6118.owner.dto.response;

import com.github.farzan6118.person.dto.response.AddressResponseDto;
import com.github.farzan6118.person.dto.response.ContactResponseDto;
import com.github.farzan6118.person.dto.response.PersonResponseDto;

import java.util.UUID;

public record OwnerResponseDto(
        UUID uuid,
        PersonResponseDto person,
        ContactResponseDto contact,
        AddressResponseDto address
) {
}
