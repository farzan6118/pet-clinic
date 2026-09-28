package com.github.farzan6118.petclinic.common.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends ApplicationException {

    public ExternalServiceException(String userMessage, String logMessage) {
        super(HttpStatus.SERVICE_UNAVAILABLE, userMessage, logMessage);
    }
}
