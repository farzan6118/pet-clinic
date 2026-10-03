package com.github.farzan6118.vet.model;

import com.github.farzan6118.clinic.model.Building;
import com.github.farzan6118.common.enums.EntityStatus;
import com.github.farzan6118.common.persistence.BaseEntity;
import com.github.farzan6118.person.model.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Audited;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Audited
@SQLRestriction("entity_status <> 'DELETED'")
public class Vet extends BaseEntity<Long> {

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(nullable = false, unique = true)
    private Person person;

    @OneToMany(mappedBy = "vet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VetAvailability> availabilities = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Building building;

    public String getFullName() {
        return this.person.getFullName();
    }

    public String getEmail() {
        return this.person.getContact().getEmail();
    }

    public String getMobileNumber() {
        return this.person.getContact().getMobileNumber();
    }

    public void setStatus(EntityStatus status) {
        this.person.getContact().setEntityStatus(status);
        this.person.getAddress().setEntityStatus(status);
        this.person.setEntityStatus(status);
        this.setEntityStatus(status);
    }
}
