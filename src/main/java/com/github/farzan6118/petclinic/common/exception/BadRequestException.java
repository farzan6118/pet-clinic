package com.github.farzan6118.petclinic.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApplicationException {

    public BadRequestException(String message, String logMessage) {
        super(HttpStatus.BAD_REQUEST, message, logMessage);
    }

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message, null);
    }
}
