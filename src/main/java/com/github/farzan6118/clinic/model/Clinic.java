package com.github.farzan6118.clinic.model;

import com.github.farzan6118.common.persistence.BaseEntity;
import com.github.farzan6118.person.model.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("entity_status <> 'DELETED'")
public class Clinic extends BaseEntity<Integer> {

    @Column(nullable = false, length = 100)
    private String name;

    @Min(1)
    @Max(10)
    @Column(nullable = false, unique = true)
    private int code;

    @OneToOne(
            cascade = CascadeType.ALL,
            optional = false,
            orphanRemoval = true
    )
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;

    @Column(nullable = false)
    private boolean active;

}
