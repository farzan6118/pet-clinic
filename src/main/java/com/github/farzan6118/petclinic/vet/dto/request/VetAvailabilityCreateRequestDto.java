package com.github.farzan6118.petclinic.vet.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record VetAvailabilityCreateRequestDto(

        @NotNull
        UUID vetUuid,

        @NotNull
        @Schema(example = "09:00")
        @JsonFormat(pattern = "HH:mm")
        @FutureOrPresent
        LocalDateTime startTime,

        @Future
        @NotNull
        @Schema(example = "17:00")
        @JsonFormat(pattern = "HH:mm")
        LocalDateTime endTime
) {
}
