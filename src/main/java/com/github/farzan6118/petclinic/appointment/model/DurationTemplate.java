package com.github.farzan6118.petclinic.appointment.model;

import com.github.farzan6118.petclinic.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
@SQLRestriction("entity_status <> 'DELETED'")
public class DurationTemplate extends BaseEntity<Integer> {

    @Length(max = 32)
    @Column(nullable = false, unique = true)
    private String name;

    @PositiveOrZero
    @Column(nullable = false, unique = true)
    private Integer durationMinutes;

    @Length(max = 64)
    private String description;
}