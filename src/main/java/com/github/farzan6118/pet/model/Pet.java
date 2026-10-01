package com.github.farzan6118.pet.model;

import com.github.farzan6118.common.enums.Sex;
import com.github.farzan6118.common.persistence.BaseEntity;
import com.github.farzan6118.owner.model.Owner;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@SQLRestriction("entity_status <> 'DELETED'")
public class Pet extends BaseEntity<Long> {

    @Size(max = 128)
    @Column(nullable = false)
    private String name;

    @Size(max = 64)
    private String color;

    private String marks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    private LocalDate birthDate;

}
