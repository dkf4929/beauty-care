package com.project.beauty_care.global.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Errors {
    // login / auth
    BAD_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED, null),
    AUTHORITY_NOT(HttpStatus.FORBIDDEN, ErrorCodes.FORBIDDEN, "해당 API를 호출할 권한이 없습니다."),
    NOT_LOGIN_USER(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED, "로그인 후 진행하세요."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),

    // jwt
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED_TOKEN_SIGNATURE_INVALID, "Token Signature Invalid"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED_TOKEN_EXPIRED, "Token Expired"),;

    private final HttpStatus httpStatus;
    private final ErrorCodes errorCode;
    private final String message;
}
