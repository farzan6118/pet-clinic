package com.github.farzan6118.petclinic.appointment.model;

import com.github.farzan6118.petclinic.clinic.model.Room;
import com.github.farzan6118.petclinic.common.enums.VisitStatus;
import com.github.farzan6118.petclinic.common.enums.VisitType;
import com.github.farzan6118.petclinic.common.exception.BadRequestException;
import com.github.farzan6118.petclinic.common.exception.ConflictException;
import com.github.farzan6118.petclinic.common.persistence.BaseEntity;
import com.github.farzan6118.petclinic.common.valueobject.DateTimeRange;
import com.github.farzan6118.petclinic.pet.model.Pet;
import com.github.farzan6118.petclinic.vet.model.Vet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Audited;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Audited
@NoArgsConstructor
@SQLRestriction("entity_status <> 'DELETED'")
public class Visit extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vet_id", nullable = false)
    private Vet vet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitType visitType;

    @Embedded
    private DateTimeRange timeRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitStatus status;

    @Column(length = 1000)
    private String description;

    private Instant bookedAt;


    public Visit schedule(Vet vet, Pet pet, Room room, LocalDateTime startTime, LocalDateTime endTime,
                          VisitType visitType, String description) {
        DateTimeRange timeRange = validatedTimeRange(startTime, endTime);
        Visit visit = new Visit();
        visit.vet = vet;
        visit.pet = pet;
        visit.room = room;
        visit.timeRange = timeRange;
        visit.visitType = visitType;
        visit.description = description;
        visit.status = VisitStatus.SCHEDULED;
        visit.bookedAt = Instant.now();
        return visit;
    }

    public void reschedule(Room room, LocalDateTime startTime, LocalDateTime endTime,
                           VisitType visitType, String description) {
        DateTimeRange timeRange = validatedTimeRange(startTime, endTime);
        this.room = room;
        this.timeRange = timeRange;
        this.visitType = visitType;

        if (description != null) {
            this.description = description;
        }
    }

    public void cancel() {

        if (status == VisitStatus.COMPLETED) {
            throw new ConflictException("Completed visit cannot be cancelled");
        }

        if (status == VisitStatus.CANCELLED) {
            return;
        }

        this.status = VisitStatus.CANCELLED;
    }

    public void complete(LocalDateTime end) {

        if (status == VisitStatus.CANCELLED) {
            throw new ConflictException("Cancelled visit cannot be completed");
        }

        if (status == VisitStatus.COMPLETED) {
            throw new ConflictException("Visit is already completed");
        }

        this.timeRange = validatedTimeRange(timeRange.getStartDateTime(), end);
        this.status = VisitStatus.COMPLETED;
    }

    private DateTimeRange validatedTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeRange range = new DateTimeRange(startTime, endTime);
        if (!range.isValid()) {
            throw new BadRequestException("End time must be after start time");
        }

        if (!range.isSameDay()) {
            throw new BadRequestException("Visit start and end must be on the same day");
        }
        return range;
    }

    public LocalDateTime getStartTime() {
        return timeRange == null ? null : timeRange.getStartDateTime();
    }

    public LocalDateTime getEndTime() {
        return timeRange == null ? null : timeRange.getEndDateTime();
    }
}
