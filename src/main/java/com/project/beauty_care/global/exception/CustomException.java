package com.project.beauty_care.global.exception;

import com.project.beauty_care.global.enums.Errors;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final Errors errors;

    public CustomException(final Errors errors) {
        this.errors = errors;
    }
}
