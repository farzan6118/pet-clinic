package com.github.farzan6118.petclinic.common.exception;

import java.time.Instant;

public record ErrorResponseDto(
        int status,
        String error,
        String message,
        Instant timestamp,
        String path
) {
}