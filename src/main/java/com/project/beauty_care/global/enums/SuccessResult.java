package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessResult {
    // login
    LOGIN_SUCCESS("L001", "로그인 성공"),
    SAVE_SUCCESS("S001", "저장이 완료 되었습니다."),
    DELETE_SUCCESS("D001", "삭제 되었습니다."),
    RETRIEVE_SUCCESS("R001", "조회가 완료 되었습니다."),

    // member
    MEMBER_SAVE_SUCCESS("M001", "회원가입이 완료 되었습니다.");

    private final String code;
    private final String message;
}
