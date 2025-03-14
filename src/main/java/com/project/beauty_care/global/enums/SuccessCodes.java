package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessCodes {
    // login
    LOGIN_SUCCESS("200", "로그인 성공"),
    SAVE_SUCCESS("201", "저장이 완료 되었습니다."),
    DELETE_SUCCESS("204", "삭제 되었습니다."),
    RETRIEVE_SUCCESS("200", "조회가 완료 되었습니다."),

    // member
    MEMBER_SAVE_SUCCESS("201", "회원 저장이 완료 되었습니다.");

    private final String code;
    private final String message;
}
