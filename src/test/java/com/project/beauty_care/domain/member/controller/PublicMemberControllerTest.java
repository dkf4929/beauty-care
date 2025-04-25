package com.project.beauty_care.domain.member.controller;

import com.project.beauty_care.ControllerTestSupport;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.ErrorCodes;
import com.project.beauty_care.global.enums.SuccessCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicMemberControllerTest extends ControllerTestSupport {
    @DisplayName("사용자 정보를 입력해서, 회원가입 한다.")
    @ParameterizedTest
    @MethodSource("com.project.beauty_care.RequestProviderFactory#validProvider")
    void createMember(PublicMemberCreateRequest request) throws Exception {
        // given
        when(memberService.createMemberPublic(any()))
                .thenReturn(
                        Member.createForTest(1L, "test", "1234", "test", buildRole(Authentication.USER.getName()))
                );

        // when, then
        performPost("/public/member", request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.successMessage").value(SuccessCodes.MEMBER_SAVE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.successCode").value(SuccessCodes.MEMBER_SAVE_SUCCESS.getCode().value()));
    }

    @DisplayName("사용자 정보를 입력하지 않고, 회원가입 시도하면, 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("com.project.beauty_care.RequestProviderFactory#emptyFieldProvider")
    void createMemberWithEmptyRequest(PublicMemberCreateRequest request) throws Exception {
        // when, then
        performPost("/public/member", request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("비밀번호는 필수입니다")))
                .andExpect(jsonPath("$.message", containsString("비밀번호는 8~16자의 영문 + 숫자 조합이어야 합니다")))
                .andExpect(jsonPath("$.message", containsString("로그인 ID는 필수입니다")))
                .andExpect(jsonPath("$.message", containsString("이름은 필수입니다")))
                .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()));
    }

    @DisplayName("잘못된 비밀번호 패턴으로 회원가입 시도하면, 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("com.project.beauty_care.RequestProviderFactory#invalidPasswordPatternProvider")
    void createMemberWithInvalidPassword(PublicMemberCreateRequest request) throws Exception {
        // when, then
        performPost("/public/member", request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 8~16자의 영문 + 숫자 조합이어야 합니다"))
                .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()));
    }

    @DisplayName("로그인 ID 4~10 자리의 문자")
    @ParameterizedTest
    @MethodSource("com.project.beauty_care.RequestProviderFactory#invalidLoginIdProvider")
    void createMemberWithInvalidLoginId(PublicMemberCreateRequest request) throws Exception {
        // when, then
        performPost("/public/member", request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("아이디는 4~10자리의 문자 형태로 입력해야 합니다")))
                .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()));
    }

    @DisplayName("로그인 ID 4~10 자리의 문자")
    @ParameterizedTest
    @MethodSource("com.project.beauty_care.RequestProviderFactory#invalidNameProvider")
    void createMemberWithInvalidName(PublicMemberCreateRequest request) throws Exception {
        // when, then
        performPost("/public/member", request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("이름은 2~20자리의 문자 형태로 입력해야 합니다")))
                .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()));
    }

    private ResultActions performPost(String url, Object request) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private Role buildRole(String role) {
        return Role.builder()
                .roleName(role)
                .urlPatterns(Collections.emptyMap())
                .build();
    }
}