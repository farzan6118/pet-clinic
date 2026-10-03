package com.github.farzan6118.vet.dto.request;

import com.github.farzan6118.person.dto.request.AddressCreateRequestDto;
import com.github.farzan6118.person.dto.request.ContactCreateRequestDto;
import com.github.farzan6118.person.dto.request.PersonCreateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VetCreateRequestDto(
        @Valid @NotNull PersonCreateRequestDto person,
        @Valid @NotNull ContactCreateRequestDto contact,
        @Valid @NotNull AddressCreateRequestDto address,
        UUID clinicUuid
) {
}
