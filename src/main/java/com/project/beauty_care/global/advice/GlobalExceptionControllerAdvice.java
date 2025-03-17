package com.project.beauty_care.global.advice;

import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.dto.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
@Log4j2
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionControllerAdvice {
    @ExceptionHandler(Exception.class)
    protected ResponseEntity handlerException(Exception e) {
        return handleException(e, Errors.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<ErrorResponse> handleException(Exception e, Errors error) {
        String errorMessage = Optional.ofNullable(e.getLocalizedMessage()).orElse(error.getMessage());
        HttpStatus statusCode = error.getHttpStatus();
        ErrorResponse errorResponse = ErrorResponse.of(error.getErrorCode(), errorMessage);

        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
