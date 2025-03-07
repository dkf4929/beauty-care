package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UniqueConstraint {
    UQ_MEMBER_LOGIN_ID("이미 존재하는 아이디 입니다.");

    final String message;
}
