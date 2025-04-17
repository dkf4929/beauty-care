package com.project.beauty_care.domain.code;

import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.springframework.stereotype.Component;

@Component
public class CodeValidator {
    public void checkIsDeletable(Code entity) {
        if (!entity.getChildren().isEmpty()) throw new RequestInvalidException(Errors.CAN_NOT_DELETE_CODE);
    }
}
