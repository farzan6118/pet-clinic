package com.github.farzan6118.petclinic.pet.model;

import com.github.farzan6118.petclinic.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLRestriction("entity_status <> 'DELETED'")
public class Species extends BaseEntity<Integer> {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    private String origin;

    @Column(columnDefinition = "TEXT")
    private String description;
}