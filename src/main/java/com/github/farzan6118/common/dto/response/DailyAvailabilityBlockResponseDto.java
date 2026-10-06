package com.github.farzan6118.common.dto.response;

import com.github.farzan6118.common.enums.AvailabilityStatus;

import java.time.LocalTime;
import java.util.UUID;

public record DailyAvailabilityBlockResponseDto(
        LocalTime start,
        LocalTime end,
        AvailabilityStatus status,
        UUID visitUuid
) {
}
