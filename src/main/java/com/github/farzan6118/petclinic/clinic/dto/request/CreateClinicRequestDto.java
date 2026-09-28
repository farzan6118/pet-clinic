package com.github.farzan6118.petclinic.clinic.dto.request;

import com.github.farzan6118.petclinic.person.dto.request.AddressCreateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateClinicRequestDto(
        @NotNull String name,
        @NotNull @Min(1) @Max(5) Integer code,
        @Valid @NotNull AddressCreateRequestDto address,
        @NotNull Boolean active
) {
}
