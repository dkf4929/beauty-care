package com.project.beauty_care.domain.board;

import com.project.beauty_care.domain.board.repository.BoardRepository;
import com.project.beauty_care.domain.enums.BoardType;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BoardValidator {
    private final BoardRepository repository;

    public void validBoardType(BoardType boardType, String role) {
        // 관리자가 아닐 경우, 공지 작성 불가
        if (boardType.equals(BoardType.NOTIFICATION) && !role.equals(Authentication.ADMIN.name()))
            throw new RequestInvalidException(Errors.CAN_NOT_WRITE_BOARD_NOTIFICATION);
    }

    // 관리자가 아닐 경우, 1분에 하나의 게시물만 작성 가능.
    public void validCreatedDateTime(String roleName, Long memberId) {
        if (!roleName.equals(Authentication.ADMIN.name())) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowMinusOneMinute = now.minusMinutes(1);

            // 1분 이내에 작성한 게시물이 있는지 찾는다.
            Boolean isExists =
                    repository.existsBoardByCreatedByAndCreatedDateTimeBetween(memberId, nowMinusOneMinute, now);

            // 있으면 예외
            if (isExists)
                throw new RequestInvalidException(Errors.MUST_WRITE_BOARD_AFTER_ONE_MINUTE);
        }
    }
}
