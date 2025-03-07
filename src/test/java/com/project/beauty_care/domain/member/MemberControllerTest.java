package com.project.beauty_care.domain.member;

import com.project.beauty_care.ControllerTestSupport;
import com.project.beauty_care.domain.member.dto.MemberCreateRequest;
import com.project.beauty_care.global.enums.ErrorCodes;
import com.project.beauty_care.global.enums.SuccessResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTestSupport {
    @DisplayName("사용자 정보를 입력해서, 회원가입 한다.")
    @ParameterizedTest
    @MethodSource("validProvider")
    void createMember(MemberCreateRequest request) throws Exception {
        // given
        when(memberService.createMember(any(MemberCreateRequest.class))).thenReturn(new Member());

        // when, then
        performPost("/member", request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(SuccessResult.MEMBER_SAVE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessResult.MEMBER_SAVE_SUCCESS.getCode()));
    }

    @DisplayName("사용자 정보를 입력하지 않고, 회원가입 시도하면, 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("emptyFieldProvider")
    void createMemberWithBlankRequest(MemberCreateRequest request) throws Exception {
        // when, then
        performPost("/member", request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("비밀번호는 필수입니다")))
                .andExpect(jsonPath("$.message", containsString("비밀번호는 8~16자의 영문 + 숫자 조합이어야 합니다")))
                .andExpect(jsonPath("$.message", containsString("로그인 ID는 필수입니다")))
                .andExpect(jsonPath("$.message", containsString("이름은 필수입니다")))
                .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()));
    }

    @DisplayName("잘못된 비밀번호 패턴으로 회원가입 시도하면, 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("invalidPasswordPatternProvider")
    void createMemberWithInvalidPassword(MemberCreateRequest request) throws Exception {
        // when, then
        performPost("/member", request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 8~16자의 영문 + 숫자 조합이어야 합니다"))
                .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()));
    }

    @DisplayName("로그인 ID 4~10 자리의 문자")
    @ParameterizedTest
    @MethodSource("invalidLoginIdProvider")
    void createMemberWithInvalidLoginId(MemberCreateRequest request) throws Exception {
        // when, then
        performPost("/member", request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("아이디는 4~10자리의 문자 형태로 입력해야 합니다")))
                .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()));
    }

    @DisplayName("로그인 ID 4~10 자리의 문자")
    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    void createMemberWithInvalidName(MemberCreateRequest request) throws Exception {
        // when, then
        performPost("/member", request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("이름은 2~20자리의 문자 형태로 입력해야 합니다")))
                .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()));
    }

    private ResultActions performPost(String url, Object request) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private static Stream<Arguments> validProvider() {
        return Stream.of(
                Arguments.of(new MemberCreateRequest("admin", "qwer1234", "admin")),
                Arguments.of(new MemberCreateRequest("user", "qwer12345", "user")),
                Arguments.of(new MemberCreateRequest("user1", "qwer123456", "user1"))
        );
    }

    private static Stream<Arguments> invalidPasswordPatternProvider() {
        return Stream.of(
                // 최소 8글자
                Arguments.of(new MemberCreateRequest("admin", "12345", "admin")),
                // 영문 + 숫자 조합
                Arguments.of(new MemberCreateRequest("admin", "12345678", "admin")),
                // 최대 16글자
                Arguments.of(new MemberCreateRequest("admin", "12345678123456789", "admin"))
        );
    }


    private static Stream<Arguments> emptyFieldProvider() {
        return Stream.of(
                Arguments.of(new MemberCreateRequest("", "", ""))
        );
    }

    private static Stream<Arguments> invalidLoginIdProvider() {
        return Stream.of(
                // ID 최소 4
                Arguments.of(new MemberCreateRequest("dd", "qwer1234", "user")),
                // ID 최대 10
                Arguments.of(new MemberCreateRequest("ddddddddddd", "qwer1234", "user"))
        );
    }

    private static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of(new MemberCreateRequest("user", "qwer1234", "d")),
                Arguments.of(new MemberCreateRequest("user", "qwer1234", "ddddddddddddddddddddd"))
        );
    }
}