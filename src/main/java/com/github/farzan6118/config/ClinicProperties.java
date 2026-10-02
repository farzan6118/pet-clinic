package com.github.farzan6118.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@ConfigurationProperties(prefix = "clinic.availability")
public record ClinicProperties(
        WorkingHours workingHours,
        Set<DayOfWeek> closeDays,
        int timeBlockMinutes
) {
    public record WorkingHours(
            LocalTime start,
            LocalTime end
    ) {
    }
}
