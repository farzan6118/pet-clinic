package com.github.farzan6118.petclinic.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponseDto> handleApplicationException(
            ApplicationException ex, HttpServletRequest request) {
        log.warn("Request rejected. status={}, path={}, detail={}",
                ex.getStatus().value(), request.getRequestURI(),
                ex.getLogMessage() == null ? ex.getMessage() : ex.getLogMessage());
        return buildResponse(ex.getStatus(), ex.getUserMessage(), request);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindingException(BindException ex, HttpServletRequest request) {
        Map<String, TreeSet<String>> errors = new TreeMap<>();
        ex.getFieldErrors().forEach(error -> errors
                .computeIfAbsent(error.getField(), ignored -> new TreeSet<>())
                .add(error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage()));

        String message = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + String.join("; ", entry.getValue()))
                .reduce((left, right) -> left + ", " + right)
                .orElse("Request validation failed");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .distinct()
                .sorted()
                .reduce((left, right) -> left + ", " + right)
                .orElse("Request validation failed");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleUnreadableMessage(HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Invalid value for parameter '" + ex.getName() + "'", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity conflict. path={}, cause={}", request.getRequestURI(), ex.getClass().getSimpleName());
        return buildResponse(HttpStatus.CONFLICT, "Request conflicts with existing data", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication required", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMediaTypeNotSupported(HttpServletRequest request) {
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type", request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        log.warn(
                "HTTP exception. status={}, reason={}, path={}",
                ex.getStatusCode(), ex.getReason(), request.getRequestURI());

        HttpStatusCode status = ex.getStatusCode();
        String reason = ex.getReason() == null ? status.toString() : ex.getReason();
        return buildResponse(status, reason, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected exception. path={}", request.getRequestURI(), ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", request);
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(
            HttpStatusCode status, String message, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto(
                status.value(),
                status.toString(),
                message,
                Instant.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }
}
