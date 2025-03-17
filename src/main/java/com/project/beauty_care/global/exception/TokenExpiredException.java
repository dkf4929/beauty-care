package com.project.beauty_care.global.exception;

import com.project.beauty_care.global.enums.Errors;

public class TokenExpiredException extends CustomException {
    public TokenExpiredException(Errors errors) {
        super(errors);
    }
}
