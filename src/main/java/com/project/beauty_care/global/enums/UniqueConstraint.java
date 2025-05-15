package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UniqueConstraint {
    UQ_MEMBER_LOGIN_ID("이미 존재하는 아이디 입니다."),
    UQ_CODE_UPPER_ID_AND_NAME("동일한 레벨의 코드에 중복되는 명칭이 존재합니다."),
    UQ_BOARD_ID_AND_MEMBER_ID("이미 신고한 게시물 입니다.");

    final String message;
}
