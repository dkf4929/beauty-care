package com.project.beauty_care.domain.member.controller;

import com.project.beauty_care.ControllerTestSupport;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.AdminMemberCreateRequest;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
import com.project.beauty_care.global.enums.ErrorCodes;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.enums.SuccessCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminMemberControllerTest extends ControllerTestSupport {
    final String RETRIEVE_SUCCESS_MESSAGE = "조회가 완료 되었습니다.";
    final String LOGIN_ID_SIZE_INVALID = "아이디는 4~10자리의 문자 형태로 입력해야 합니다";
    final String LOGIN_ID_ESSENTIAL = "로그인 ID는 필수입니다";
    final String NAME_ESSENTIAL = "이름은 필수입니다";
    final String NAME_SIZE_INVALID = "이름은 2~20자리의 문자 형태로 입력해야 합니다";

    @DisplayName("모든 멤버 조회")
    @Test
    void findAllMembers() throws Exception {
        when(memberService.findAllMembers())
                .thenReturn(buildAllMembers());

        // when, then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/admin/member")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCode").value("200"))
                .andExpect(jsonPath("$.successMessage").value(RETRIEVE_SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @DisplayName("관리자가 사용자를 생성한다.")
    @Test
    void createMember() throws Exception {
        // given
        final String loginId = "user";
        final String name = "user";

        AdminMemberCreateRequest request = buildCreateRequest(loginId, name, Role.USER);

        // when
        when(memberService.createMemberAdmin(any()))
                .thenReturn(buildMember(loginId, name, Role.USER));

        mockMvc.perform(
                        post("/admin/member")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.successCode").value(SuccessCodes.MEMBER_SAVE_SUCCESS.getCode()))
                .andExpect(jsonPath("$.successMessage").value(SuccessCodes.MEMBER_SAVE_SUCCESS.getMessage()));
    }

    @DisplayName("필수값 미입력 시, 예외 발생")
    @TestFactory
    Collection<DynamicTest> createMemberWithInvalidRequest() throws Exception {
        return List.of(
                DynamicTest.dynamicTest("로그인 ID 미입력 => 예외 발생", () ->
                {
                    AdminMemberCreateRequest request = buildCreateRequest("", "user", Role.USER);

                    mockMvc.perform(
                                    post("/admin/member")
                                            .content(objectMapper.writeValueAsString(request))
                                            .contentType(MediaType.APPLICATION_JSON)
                            )
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()))
                            .andExpect(jsonPath("$.message", containsString(LOGIN_ID_SIZE_INVALID)))
                            .andExpect(jsonPath("$.message", containsString(LOGIN_ID_ESSENTIAL)));
                }),
                DynamicTest.dynamicTest("로그인 ID는 4~10자리 문자 형태", () ->
                {
                    AdminMemberCreateRequest request = buildCreateRequest("ddd", "user", Role.USER);

                    mockMvc.perform(
                                    post("/admin/member")
                                            .content(objectMapper.writeValueAsString(request))
                                            .contentType(MediaType.APPLICATION_JSON)
                            )
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()))
                            .andExpect(jsonPath("$.message").value(LOGIN_ID_SIZE_INVALID));
                }),
                DynamicTest.dynamicTest("사용자명 미입력 => 예외 발생", () ->
                {
                    AdminMemberCreateRequest request = buildCreateRequest("user", "", Role.USER);

                    mockMvc.perform(
                                    post("/admin/member")
                                            .content(objectMapper.writeValueAsString(request))
                                            .contentType(MediaType.APPLICATION_JSON)
                            )
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()))
                            .andExpect(jsonPath("$.message", containsString(NAME_SIZE_INVALID)))
                            .andExpect(jsonPath("$.message", containsString(NAME_ESSENTIAL)));
                }),
                DynamicTest.dynamicTest("사용자명 2~20자리의 문자 형태", () ->
                {
                    AdminMemberCreateRequest request = buildCreateRequest("user", "d", Role.USER);

                    mockMvc.perform(
                                    post("/admin/member")
                                            .content(objectMapper.writeValueAsString(request))
                                            .contentType(MediaType.APPLICATION_JSON)
                            )
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()))
                            .andExpect(jsonPath("$.message").value(NAME_SIZE_INVALID));
                })
        );
    }

    private static Member buildMember(String loginId, String name, Role role) {
        return Member.builder()
                .loginId(loginId)
                .name(name)
                .role(role)
                .build();
    }

    private List<MemberResponse> buildAllMembers() {
        return List.of(
                MemberResponse.builder()
                        .isUse(Boolean.TRUE)
                        .id(1L)
                        .name("admin")
                        .role(Role.ADMIN.getValue())
                        .loginId("admin")
                        .build(),
                MemberResponse.builder()
                        .isUse(Boolean.TRUE)
                        .id(2L)
                        .name("user")
                        .role(Role.USER.getValue())
                        .loginId("user")
                        .build()
        );
    }

    private AdminMemberCreateRequest buildCreateRequest(String loginId, String name, Role role) {
        return AdminMemberCreateRequest.builder()
                .loginId(loginId)
                .name(name)
                .role(role)
                .isUse(Boolean.TRUE)
                .build();
    }
}
