package com.github.farzan6118.vet.model;

import com.github.farzan6118.common.exception.BadRequestException;
import com.github.farzan6118.common.persistence.BaseEntity;
import com.github.farzan6118.common.valueobject.DateTimeRange;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SQLRestriction("entity_status <> 'DELETED'")
public class VetAvailability extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Vet vet;

    @Embedded
    private DateTimeRange timeRange;

    @Column(nullable = false)
    private boolean active = true;


    public static VetAvailability create(Vet vet, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        DateTimeRange timeRange = new DateTimeRange(startDateTime, endDateTime);
        validateDateAndTime(timeRange);
        VetAvailability availability = new VetAvailability();
        availability.vet = vet;
        availability.timeRange = timeRange;
        availability.active = true;
        return availability;
    }

    private static void validateDateAndTime(DateTimeRange timeRange) {
        if (!timeRange.isValid() || timeRange.isNotSameDay()) {
            throw new BadRequestException(
                    "Start and end date-time must be valid and on the same day"
            );
        }
        if (timeRange.getDuration().toMinutes() < 2) {
            throw new BadRequestException(
                    "Availability duration must be at least 2 minutes"
            );
        }
    }

    public void update(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        DateTimeRange timeRange = new DateTimeRange(startDateTime, endDateTime);
        validateDateAndTime(timeRange);
        this.timeRange = timeRange;
    }
}
