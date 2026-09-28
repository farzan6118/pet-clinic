package com.github.farzan6118.petclinic.vet.dto.request;

import com.github.farzan6118.petclinic.person.dto.request.AddressUpdateRequestDto;
import com.github.farzan6118.petclinic.person.dto.request.PersonUpdateRequestDto;
import com.github.farzan6118.petclinic.person.dto.request.ProfileUpdateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record VetUpdateRequestDto(
        @Valid @NotNull PersonUpdateRequestDto person,
        @Valid @NotNull ProfileUpdateRequestDto profile,
        @Valid @NotNull AddressUpdateRequestDto address
) {
}
