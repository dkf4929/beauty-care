package com.project.beauty_care.global.exception;

import com.project.beauty_care.global.enums.Errors;

public class RequestInvalidException extends CustomException {
    public RequestInvalidException(Errors errors) {
        super(errors);
    }
}
