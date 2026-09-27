package com.github.farzan6118.petclinic.appointment.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.farzan6118.petclinic.common.enums.VisitType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateVisitRequestDto(
        @NotNull(message = "pet.uuid.is.required")
        UUID petUuid,
        @NotNull(message = "vet.uuid.is.required")
        UUID vetUuid,
        @NotNull(message = "visit.date.is.required")
        @FutureOrPresent(message = "visit.date.must.be.in.present.or.future")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate visitDate,
        @NotNull(message = "visit.time.is.required")
        @JsonFormat(pattern = "HH:mm")
        @Schema(example = "09:30")
        LocalTime visitTime,
        @NotNull(message = "visit.type.is.required")
        VisitType visitType,
        @Min(2)
        @Min(120)
        Integer durationMinutes,
        @Size(max = 2048, message = "description.too.long")
        String description

) {
}
