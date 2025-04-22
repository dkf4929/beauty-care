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
    NO_AUTHORITY_MEMBER(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode(), "권한이 부여되지 않은 사용자입니다."),

    // jwt
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED_TOKEN_SIGNATURE_INVALID.getErrorCode(), "Token Signature Invalid"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED_TOKEN_EXPIRED.getErrorCode(), "Token Expired"),

    // db
    DB_UNSATISFIED_CONSTRAINT(HttpStatus.CONFLICT, ErrorCodes.DB_UNSATISFIED_CONSTRAINT.getErrorCode(), ""),
    BAD_REQUEST_INVALID_VALUE(HttpStatus.BAD_REQUEST, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "요청 값이 잘못되었습니다."),

    // NOT FOUND ENTITY
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "등록된 사용자가 아닙니다."),
    NOT_FOUND_CODE(HttpStatus.NOT_FOUND, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "등록된 코드가 아닙니다."),
    NOT_FOUND_PARENT_CODE(HttpStatus.NOT_FOUND, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "상위 코드를 찾을 수 없습니다."),
    NOT_FOUND_ROLE(HttpStatus.NOT_FOUND, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "등록된 권한이 아닙니다."),
    NOT_FOUND_MENU(HttpStatus.NOT_FOUND, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "등록된 메뉴가 아닙니다."),
    NOT_FOUND_PARENT_MENU(HttpStatus.NOT_FOUND, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "상위 메뉴를 찾을 수 없습니다."),

    // request invalid
    CAN_NOT_DELETE_CODE(HttpStatus.BAD_REQUEST, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "하위코드가 존재하는 경우, 삭제할 수 없습니다."),
    DUPLICATED_CODE(HttpStatus.BAD_REQUEST, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "중복된 코드 ID가 존재합니다."),
    MAX_MENU_DEPTH_ERROR(HttpStatus.BAD_REQUEST, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "메뉴는 최대 3depth 입니다."),
    PARENT_MENU_NOT_USE(HttpStatus.BAD_REQUEST, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "상위 메뉴가 사용중 상태가 아닙니다."),
    DUPLICATED_ROLE(HttpStatus.CONFLICT, ErrorCodes.DB_UNSATISFIED_CONSTRAINT.getErrorCode(), "중복된 권한이 존재합니다."),
    CAN_NOT_UPDATE_ADMIN_ROLE(HttpStatus.BAD_REQUEST, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "관리자 권한을 가진 사용자는 수정할 수 없습니다."),
    MUST_UPDATE_PRIVATE_PAGE(HttpStatus.BAD_REQUEST, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "개인정보 수정은 사용자 기능입니다."),
    CAN_NOT_BE_LEAF_MENU(HttpStatus.BAD_REQUEST, ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode(), "메뉴는 3depth 입니다. 하위 메뉴가 될 수 없는 계층입니다."),

    //redis
    REDIS_CACHE_KEY_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode(), "캐시 키가 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_SERVER_ERROR.getErrorCode(), "서버에 오류가 발생했습니다. 관리자에게 문의하세요.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
