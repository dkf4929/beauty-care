package com.project.beauty_care.global;

import com.project.beauty_care.global.enums.ErrorCodes;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.UniqueConstraint;
import com.project.beauty_care.global.exception.CustomException;
import com.project.beauty_care.global.exception.dto.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@Log4j2
public class ExceptionControllerAdvice {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity handlerCustomException(CustomException e) {
        return handleException(e, e.getErrors());
    }

    //@Valid, @Validated
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorMessages = new ArrayList<>();

        e.getBindingResult().getFieldErrors()
                .stream()
                .forEach(fieldError -> errorMessages.add(fieldError.getDefaultMessage()));

        ErrorResponse errorResponse = ErrorResponse.of(Errors.BAD_REQUEST_INVALID_VALUE.getErrorCode(), String.join(", ", errorMessages));
        log.error(e.toString());
        log.error(Arrays.toString(e.getStackTrace()));

        return ResponseEntity.status(Errors.BAD_REQUEST_INVALID_VALUE.getHttpStatus()).body(errorResponse);
    }

    // 제약조건
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String constraintName = extractConstraint(e.getMessage());
        Errors errors = Errors.DB_UNSATISFIED_CONSTRAINT;
        String errorMessage;

        if (StringUtils.isNotBlank(constraintName) &&
                Arrays.stream(UniqueConstraint.values())
                        .anyMatch(uniqueConstraint -> uniqueConstraint.name().equals(constraintName)))
            errorMessage = UniqueConstraint.valueOf(constraintName).getMessage();
        else errorMessage = "DB 제약조건을 위반하였습니다.";

        ErrorResponse errorResponse = ErrorResponse.of(errors.getErrorCode(), errorMessage);

        log.error(e.toString());
        log.error(Arrays.toString(e.getStackTrace()));

        return ResponseEntity.status(errors.getHttpStatus()).body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> handleException(Exception e, Errors error) {
        String errorMessage = Optional.ofNullable(e.getLocalizedMessage()).orElse(error.getMessage());
        HttpStatus statusCode = error.getHttpStatus();
        ErrorResponse errorResponse = ErrorResponse.of(error.getErrorCode(), errorMessage);

        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    private String extractConstraint(String message) {
        Pattern pattern = Pattern.compile("for key '(.+?)'");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }
}
