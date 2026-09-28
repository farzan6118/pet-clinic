package com.github.farzan6118.petclinic.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String userMessage;
    private final String logMessage;

    protected ApplicationException(HttpStatus status, String userMessage, String logMessage) {
        super(userMessage);
        this.status = status;
        this.userMessage = userMessage;
        this.logMessage = logMessage;
    }
}
