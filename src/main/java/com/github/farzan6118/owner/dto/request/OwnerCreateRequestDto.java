package com.github.farzan6118.owner.dto.request;

import com.github.farzan6118.person.dto.request.AddressCreateRequestDto;
import com.github.farzan6118.person.dto.request.PersonCreateRequestDto;
import com.github.farzan6118.person.dto.request.ProfileCreateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OwnerCreateRequestDto(
        @Valid @NotNull PersonCreateRequestDto person,
        @Valid @NotNull ProfileCreateRequestDto profile,
        @Valid @NotNull AddressCreateRequestDto address
) {
}
