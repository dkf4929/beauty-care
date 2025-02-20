package com.project.beauty_care.global.exception;

import com.project.beauty_care.global.enums.Errors;

public class JwtException extends CustomException {
    public JwtException(Errors errors) {
        super(errors);
    }
}
