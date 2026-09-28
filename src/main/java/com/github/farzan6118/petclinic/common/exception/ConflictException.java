package com.github.farzan6118.petclinic.common.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApplicationException {

    public ConflictException(String message, String logMessage) {
        super(HttpStatus.CONFLICT, message, logMessage);
    }

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message, null);
    }
}
