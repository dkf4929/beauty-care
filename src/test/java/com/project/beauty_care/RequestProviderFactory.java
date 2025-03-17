package com.project.beauty_care;

import com.project.beauty_care.domain.member.dto.MemberCreateRequest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class RequestProviderFactory {
    // MemberController
    public static Stream<Arguments> validProvider() {
        return Stream.of(
                Arguments.of(new MemberCreateRequest("admin", "qwer1234", "admin")),
                Arguments.of(new MemberCreateRequest("user", "qwer12345", "user")),
                Arguments.of(new MemberCreateRequest("user1", "qwer123456", "user1"))
        );
    }

    public static Stream<Arguments> invalidPasswordPatternProvider() {
        return Stream.of(
                Arguments.of(new MemberCreateRequest("admin", "12345", "admin")),
                Arguments.of(new MemberCreateRequest("admin", "12345678", "admin")),
                Arguments.of(new MemberCreateRequest("admin", "12345678123456789", "admin")),
                Arguments.of(new MemberCreateRequest("admin", "aa", "admin")),
                Arguments.of(new MemberCreateRequest("admin", "aaaaaaaa", "admin"))
        );
    }

    public static Stream<Arguments> emptyFieldProvider() {
        return Stream.of(
                Arguments.of(new MemberCreateRequest("", "", ""))
        );
    }

    public static Stream<Arguments> invalidLoginIdProvider() {
        return Stream.of(
                Arguments.of(new MemberCreateRequest("dd", "qwer1234", "user")),
                Arguments.of(new MemberCreateRequest("ddddddddddd", "qwer1234", "user"))
        );
    }

    // token test
    public static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of(new MemberCreateRequest("user", "qwer1234", "d")),
                Arguments.of(new MemberCreateRequest("user", "qwer1234", "ddddddddddddddddddddd"))
        );
    }
}
