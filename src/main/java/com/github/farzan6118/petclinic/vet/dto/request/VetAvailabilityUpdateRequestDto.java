package com.github.farzan6118.petclinic.vet.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record VetAvailabilityUpdateRequestDto(

        @NotNull
        UUID vetUuid,

        @NotNull
        @Schema(example = "2026-10-02T09:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @FutureOrPresent
        LocalDateTime startTime,

        @Future
        @NotNull
        @Schema(example = "2026-10-02T17:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endTime
) {
}
