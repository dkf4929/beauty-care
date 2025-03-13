package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessCodes {
    // login
    LOGIN_SUCCESS("S001", "로그인 성공"),
    SAVE_SUCCESS("S002", "저장이 완료 되었습니다."),
    DELETE_SUCCESS("S003", "삭제 되었습니다."),
    RETRIEVE_SUCCESS("S004", "조회가 완료 되었습니다."),

    // member
    MEMBER_SAVE_SUCCESS("S005", "회원가입이 완료 되었습니다.");

    private final String code;
    private final String message;
}
