package com.github.farzan6118.petclinic.vet.model;

import com.github.farzan6118.petclinic.clinic.model.Clinic;
import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import com.github.farzan6118.petclinic.common.persistence.BaseEntity;
import com.github.farzan6118.petclinic.person.model.Person;
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
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;

    @OneToMany(mappedBy = "vet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VetAvailability> availabilities = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    public String getFullName() {
        return this.person.getFullName();
    }

    public String getEmail() {
        return this.person.getProfile().getEmail();
    }

    public String getMobileNumber() {
        return this.person.getProfile().getMobileNumber();
    }

    public void setStatus(EntityStatus status) {
        this.person.getProfile().setEntityStatus(status);
        this.person.getAddress().setEntityStatus(status);
        this.person.setEntityStatus(status);
        this.setEntityStatus(status);
    }
}
