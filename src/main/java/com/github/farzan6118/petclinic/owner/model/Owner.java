package com.github.farzan6118.petclinic.owner.model;

import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import com.github.farzan6118.petclinic.common.persistence.BaseEntity;
import com.github.farzan6118.petclinic.person.model.Person;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLRestriction("entity_status <> 'DELETED'")
public class Owner extends BaseEntity<Long> {

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;

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
