package com.project.beauty_care.global.exception;

import com.project.beauty_care.global.enums.Errors;

public class EntityNotFoundException extends CustomException {
    public EntityNotFoundException(Errors errors) {
        super(errors);
    }
}
