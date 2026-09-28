package com.github.farzan6118.petclinic.common.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateTimeRange {

    @NotNull
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @NotNull
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    public Duration getDuration() {
        return Duration.between(startDateTime, endDateTime);
    }

    public LocalDate getStartDate() {
        return startDateTime.toLocalDate();
    }

    public LocalDate getEndDate() {
        return endDateTime.toLocalDate();
    }

    public LocalTime getStartTime() {
        return startDateTime.toLocalTime();
    }

    public LocalTime getEndTime() {
        return endDateTime.toLocalTime();
    }

    public boolean isValid() {
        return startDateTime != null
                && endDateTime != null
                && startDateTime.isBefore(endDateTime);
    }

    public boolean isSameDay() {
        return getStartDate().equals(getEndDate());
    }

    public boolean overlaps(
            LocalDateTime givenStartDateTime,
            LocalDateTime givenEndDateTime) {

        return givenStartDateTime != null
                && givenEndDateTime != null
                && givenEndDateTime.isAfter(startDateTime)
                && givenStartDateTime.isBefore(endDateTime);
    }

    public boolean overlaps(DateTimeRange other) {
        return other != null
                && overlaps(other.startDateTime, other.endDateTime);
    }
}
