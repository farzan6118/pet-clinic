package com.github.farzan6118.petclinic.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApplicationException {

    public ForbiddenException(String message, String logMessage) {
        super(HttpStatus.FORBIDDEN, message, logMessage);
    }

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message, null);
    }
}
