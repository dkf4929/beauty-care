package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCodes {
    // jwt
    UNAUTHORIZED_TOKEN_SIGNATURE_INVALID("E001"),
    UNAUTHORIZED_TOKEN_EXPIRED("E002"),

    // 로그인 / 권한
    UNAUTHORIZED("E0003"),
    FORBIDDEN("E0004"),

    // DB
    DB_UNSATISFIED_CONSTRAINT("E005"),

    // valid
    API_REQUEST_INVALID_VALUE("E006"),

    INTERNAL_SERVER_ERROR("E007"),
    ;

    private final String errorCode;
}
