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
    BAD_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED.getErrorCode(), null),
    AUTHORITY_NOT(HttpStatus.FORBIDDEN, ErrorCodes.FORBIDDEN.getErrorCode(), "해당 API를 호출할 권한이 없습니다."),
    NOT_LOGIN_USER(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED.getErrorCode(), "로그인 후 진행하세요."),
    PASSWORD_MISS_MATCH(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED.getErrorCode(), "비밀번호가 일치하지 않습니다."),
    ANONYMOUS_USER(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED.getErrorCode(), "가입된 회원이 아닙니다."),

    // jwt
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED_TOKEN_SIGNATURE_INVALID.getErrorCode(), "Token Signature Invalid"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED_TOKEN_EXPIRED.getErrorCode(), "Token Expired"),

    // db
    DB_UNSATISFIED_CONSTRAINT(HttpStatus.CONFLICT, ErrorCodes.DB_UNSATISFIED_CONSTRAINT.getErrorCode(), ""),
    BAD_REQUEST_INVALID_VALUE(HttpStatus.BAD_REQUEST, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "요청 값이 잘못되었습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
