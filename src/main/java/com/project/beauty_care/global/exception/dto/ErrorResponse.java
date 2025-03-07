package com.project.beauty_care.global.exception.dto;

import com.project.beauty_care.global.enums.ErrorCodes;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.YesOrNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;

    public static ErrorResponse of(Errors errors) {
        return ErrorResponse.builder()
                .code(errors.getErrorCode())
                .message(errors.getMessage())
                .build();
    }

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }
}
