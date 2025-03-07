package com.project.beauty_care.global.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorCodes {
    // jwt
    UNAUTHORIZED_TOKEN_SIGNATURE_INVALID("J001"),
    UNAUTHORIZED_TOKEN_EXPIRED("J002"),

    // 로그인 / 권한
    UNAUTHORIZED("U001"),
    FORBIDDEN("U002"),

    // DB
    DB_UNSATISFIED_CONSTRAINT("D001"),
    ;

    private final String errorCode;
}
