package com.project.beauty_care.global.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SuccessResult {
    // login
    LOGIN_SUCCESS(1001, "로그인 성공");

    private final int code;
    private final String message;
}
