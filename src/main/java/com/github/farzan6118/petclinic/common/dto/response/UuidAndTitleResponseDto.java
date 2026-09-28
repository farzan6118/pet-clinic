package com.github.farzan6118.petclinic.common.dto.response;

import java.util.UUID;

public record UuidAndTitleResponseDto(
        UUID uuid,
        String title
) {
}
