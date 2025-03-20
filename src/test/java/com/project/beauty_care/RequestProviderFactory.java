package com.project.beauty_care;

import com.project.beauty_care.domain.member.dto.AdminMemberCreateRequest;
import com.project.beauty_care.domain.member.dto.AdminMemberUpdateRequest;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
import com.project.beauty_care.global.enums.Role;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class RequestProviderFactory {
    // MemberController
    public static Stream<Arguments> validProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("admin", "qwer1234", "admin")),
                Arguments.of(new PublicMemberCreateRequest("user", "qwer12345", "user")),
                Arguments.of(new PublicMemberCreateRequest("user1", "qwer123456", "user1"))
        );
    }

    public static Stream<Arguments> invalidPasswordPatternProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("admin", "12345", "admin")),
                Arguments.of(new PublicMemberCreateRequest("admin", "12345678", "admin")),
                Arguments.of(new PublicMemberCreateRequest("admin", "12345678123456789", "admin")),
                Arguments.of(new PublicMemberCreateRequest("admin", "aa", "admin")),
                Arguments.of(new PublicMemberCreateRequest("admin", "aaaaaaaa", "admin"))
        );
    }

    public static Stream<Arguments> emptyFieldProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("", "", ""))
        );
    }

    public static Stream<Arguments> invalidLoginIdProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("dd", "qwer1234", "user")),
                Arguments.of(new PublicMemberCreateRequest("ddddddddddd", "qwer1234", "user"))
        );
    }

    // token test
    public static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of(new PublicMemberCreateRequest("user", "qwer1234", "d")),
                Arguments.of(new PublicMemberCreateRequest("user", "qwer1234", "ddddddddddddddddddddd"))
        );
    }

    public static Stream<Arguments> invalidAdminMemberCreateRequestProvider() {
        return Stream.of(
                Arguments.of(new AdminMemberCreateRequest("", "", Role.USER, true))
        );
    }

    public static Stream<Arguments> invalidAdminMemberUpdateRequestProvider() {
        AdminMemberUpdateRequest idEmpty = AdminMemberUpdateRequest.builder()
                .id(null)
                .role(Role.USER)
                .isUse(Boolean.TRUE)
                .build();

        AdminMemberUpdateRequest isUseEmpty = AdminMemberUpdateRequest.builder()
                .id(1L)
                .role(Role.USER)
                .isUse(null)
                .build();

        return Stream.of(
                Arguments.of(idEmpty, "사용자 ID를 입력하세요."),
                Arguments.of(isUseEmpty, "계정 사용 여부를 입력하세요.")
        );
    }
}
