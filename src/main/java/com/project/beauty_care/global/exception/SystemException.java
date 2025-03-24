package com.project.beauty_care.global.exception;

import com.project.beauty_care.global.enums.Errors;

public class SystemException extends CustomException {
    public SystemException(Errors errors) {
        super(errors);
    }
}
