package com.github.farzan6118.appointment.dto.response;

import com.github.farzan6118.common.enums.VisitStatus;
import com.github.farzan6118.common.enums.VisitType;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitResponseDto(
        UUID uuid,
        UUID petUuid,
        String petName,
        String species,
        String ownerFullName,
        UUID vetUuid,
        String vetFullName,
        LocalDateTime visitDateFrom,
        LocalDateTime visitDateTo,
        VisitType visitType,
        UUID roomUuid,
        String description,
        VisitStatus status
) {
}
