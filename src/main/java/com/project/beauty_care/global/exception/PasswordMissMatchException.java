package com.project.beauty_care.global.exception;

import com.project.beauty_care.global.enums.Errors;

public class PasswordMissMatchException extends CustomException {
    public PasswordMissMatchException(Errors errors) {
        super(errors);
    }
}
