package com.project.beauty_care.global;

import com.project.beauty_care.global.enums.ErrorCodes;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.CustomException;
import com.project.beauty_care.global.exception.dto.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
@Log4j2
public class ExceptionControllerAdvice {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity handlerCustomException(CustomException e) {
        return handleException(e, e.getErrors());
    }

    private ResponseEntity<ErrorResponse> handleException(Exception e, Errors error) {
        String errorMessage = Optional.ofNullable(e.getLocalizedMessage()).orElse(error.getMessage());
        ErrorCodes errorCode = error.getErrorCode();
        HttpStatus statusCode = error.getHttpStatus();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, errorMessage);

        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
