package com.github.farzan6118.petclinic.appointment.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvailableVisitSlotResponseDto(
        LocalDateTime visitDateFrom,
        LocalDateTime visitDateTo,
        UUID roomUuid
) {
}
