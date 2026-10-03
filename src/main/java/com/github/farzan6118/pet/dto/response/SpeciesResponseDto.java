package com.github.farzan6118.pet.dto.response;

import java.util.UUID;

public record SpeciesResponseDto(
        UUID uuid,
        String name,
        String code,
        String origin,
        String description
) {
}
