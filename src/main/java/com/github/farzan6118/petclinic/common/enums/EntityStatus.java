package com.github.farzan6118.petclinic.common.enums;

import lombok.Getter;

@Getter
public enum EntityStatus {
    UNINITIALIZED(0),
    ACTIVE(1),
    INACTIVE(2),
    DELETED(3);

    private final Integer id;

    EntityStatus(Integer id) {
        this.id = id;
    }
}
