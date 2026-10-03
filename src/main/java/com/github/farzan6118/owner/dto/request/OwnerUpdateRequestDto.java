package com.github.farzan6118.owner.dto.request;

import com.github.farzan6118.person.dto.request.AddressUpdateRequestDto;
import com.github.farzan6118.person.dto.request.ContactUpdateRequestDto;
import com.github.farzan6118.person.dto.request.PersonUpdateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OwnerUpdateRequestDto(
        @Valid @NotNull PersonUpdateRequestDto person,
        @Valid @NotNull ContactUpdateRequestDto contact,
        @Valid @NotNull AddressUpdateRequestDto address
) {
}
