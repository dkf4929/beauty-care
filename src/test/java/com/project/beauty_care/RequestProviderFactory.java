package com.project.beauty_care;

import com.project.beauty_care.domain.board.dto.BoardCriteria;
import com.project.beauty_care.domain.enums.BoardType;
import com.project.beauty_care.domain.member.dto.AdminMemberUpdateRequest;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
import com.project.beauty_care.global.enums.Authentication;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public abstract class RequestProviderFactory {
    // MemberController
    public static Stream<Arguments> validProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("admin", "qwer1234", "qwer1234", "admin")),
                Arguments.of(new PublicMemberCreateRequest("user", "qwer12345", "qwer12345", "user")),
                Arguments.of(new PublicMemberCreateRequest("user1", "qwer123456", "qwer123456", "user1"))
        );
    }

    public static Stream<Arguments> invalidPasswordPatternProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("admin", "12345", "qwer1234", "admin")),
                Arguments.of(new PublicMemberCreateRequest("admin", "12345678", "qwer1234", "admin")),
                Arguments.of(new PublicMemberCreateRequest("admin", "12345678123456789", "qwer1234", "admin")),
                Arguments.of(new PublicMemberCreateRequest("admin", "aa", "qwer1234", "admin")),
                Arguments.of(new PublicMemberCreateRequest("admin", "aaaaaaaa", "qwer1234", "admin"))
        );
    }

    public static Stream<Arguments> emptyFieldProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("", "", "", ""))
        );
    }

    public static Stream<Arguments> invalidLoginIdProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("dd", "qwer1234", "qwer1234", "user")),
                Arguments.of(new PublicMemberCreateRequest("ddddddddddd", "qwer1234", "qwer1234", "user"))
        );
    }

    // token test
    public static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("user", "qwer1234", "qwer1234", "d")),
                Arguments.of(new PublicMemberCreateRequest("user", "qwer1234", "qwer1234", "ddddddddddddddddddddd"))
        );
    }

    public static Stream<Arguments> boardRequestProvider() {
        return Stream.of(
                Arguments.of(Authentication.USER.name(), true, true),   // 사용자 + 1분 내 작성글 o → 예외
                Arguments.of(Authentication.USER.name(), false, false), // 사용자 + 1분 내 작성글 x → 정상
                Arguments.of(Authentication.ADMIN.name(), true, false)  // 관리자 → 정상
        );
    }

    public static Stream<Arguments> boardCriteriaRequestProvider() {
        return Stream.of(
                Arguments.of(buildBoardCriteria(BoardType.FREE, "내용1", "제목1", 1L), 1),
                Arguments.of(buildBoardCriteria(BoardType.FREE, "", "", null), 2),
                Arguments.of(buildBoardCriteria(BoardType.NOTIFICATION, "공지", "공지사항", null), 1),
                Arguments.of(buildBoardCriteria(null, "내용", "", null), 2),
                Arguments.of(buildBoardCriteria(null, "NOT FOUND", "", null), 0)
        );
    }

    public static Stream<Arguments> invalidAdminMemberUpdateRequestProvider() {
        AdminMemberUpdateRequest idEmpty = AdminMemberUpdateRequest.builder()
                .id(null)
                .role(Authentication.USER.getName())
                .isUse(Boolean.TRUE)
                .build();

        AdminMemberUpdateRequest isUseEmpty = AdminMemberUpdateRequest.builder()
                .id(1L)
                .role(Authentication.USER.getName())
                .isUse(null)
                .build();

        return Stream.of(
                Arguments.of(idEmpty, "사용자 ID를 입력하세요."),
                Arguments.of(isUseEmpty, "계정 사용 여부를 입력하세요.")
        );
    }

    private static BoardCriteria buildBoardCriteria(BoardType type,
                                             String content,
                                             String title,
                                             Long createdBy) {
        return BoardCriteria.builder()
                .boardType(type)
                .content(content)
                .title(title)
                .createdBy(createdBy)
                .build();
    }
}
