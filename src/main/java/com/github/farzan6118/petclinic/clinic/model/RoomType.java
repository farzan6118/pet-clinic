package com.github.farzan6118.petclinic.clinic.model;

import com.github.farzan6118.petclinic.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("entity_status <> 'DELETED'")
public class RoomType extends BaseEntity<Integer> {
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
}