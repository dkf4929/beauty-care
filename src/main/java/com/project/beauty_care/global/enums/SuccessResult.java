package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessResult {
    // login
    LOGIN_SUCCESS(1001, "로그인 성공");

    private final int code;
    private final String message;
}
