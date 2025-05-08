package com.project.beauty_care.domain.board;

import com.project.beauty_care.domain.enums.BoardType;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.springframework.stereotype.Component;

@Component
public class BoardValidation {
    public void validBoardType(BoardType boardType, String role) {
        // 관리자가 아닐 경우, 공지 작성 불가
        if (boardType.equals(BoardType.NOTIFICATION) && !role.equals(Authentication.ADMIN.name()))
            throw new RequestInvalidException(Errors.AUTHORITY_NOT);
    }
}
