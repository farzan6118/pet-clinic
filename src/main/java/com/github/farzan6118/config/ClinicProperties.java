package com.github.farzan6118.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@ConfigurationProperties(prefix = "clinic.availibility")
public record ClinicProperties(
        WorkingHours workingHours,
        Set<DayOfWeek> closeDays
) {
    public record WorkingHours(
            LocalTime start,
            LocalTime end
    ) {
    }
}