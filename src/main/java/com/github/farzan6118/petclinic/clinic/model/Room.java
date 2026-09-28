package com.github.farzan6118.petclinic.clinic.model;

import com.github.farzan6118.petclinic.common.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("entity_status <> 'DELETED'")
public class Room extends BaseEntity<Integer> {

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(length = 20, nullable = false)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Clinic clinic;

    @Column(nullable = false)
    private boolean active;
}