package com.github.farzan6118.owner.model;

import com.github.farzan6118.common.enums.EntityStatus;
import com.github.farzan6118.common.persistence.BaseEntity;
import com.github.farzan6118.person.model.Person;
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
    @JoinColumn(nullable = false)
    private Person person;

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
