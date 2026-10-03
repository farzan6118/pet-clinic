package com.github.farzan6118.owner.dto.request;

import com.github.farzan6118.person.dto.request.AddressCreateRequestDto;
import com.github.farzan6118.person.dto.request.ContactCreateRequestDto;
import com.github.farzan6118.person.dto.request.PersonCreateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OwnerCreateRequestDto(
        @Valid @NotNull PersonCreateRequestDto person,
        @Valid @NotNull ContactCreateRequestDto contact,
        @Valid @NotNull AddressCreateRequestDto address
) {
}
