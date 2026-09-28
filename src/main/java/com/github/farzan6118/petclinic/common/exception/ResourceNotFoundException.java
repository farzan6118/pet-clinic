package com.github.farzan6118.petclinic.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String message, String logMessage) {
        super(HttpStatus.NOT_FOUND, message, logMessage);
    }

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, null);
    }
}
