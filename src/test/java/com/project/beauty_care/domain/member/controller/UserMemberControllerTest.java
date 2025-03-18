package com.project.beauty_care.domain.member.controller;

import com.project.beauty_care.ControllerTestSupport;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.security.WithMockCustomUser;
import com.project.beauty_care.global.security.dto.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserMemberControllerTest extends ControllerTestSupport {
    final String RETRIEVE_SUCCESS_MESSAGE = "조회가 완료 되었습니다.";

    @DisplayName("로그인한 사용자의 사용자 정보를 조회한다.")
    @Test
    @WithMockCustomUser
    void myInfo() throws Exception {
        // given
        when(memberService.findMemberById(anyLong()))
                .thenReturn(MemberResponse.builder()
                        .isUse(Boolean.TRUE)
                        .id(1L)
                        .name("admin")
                        .role(Role.ADMIN.getValue())
                        .loginId("admin")
                        .build());

        // when, then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/user/member")
        )
        .andDo(print())
        .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCode").value("200"))
                .andExpect(jsonPath("$.successMessage").value(RETRIEVE_SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.loginId").value("admin"))
                .andExpect(jsonPath("$.data.name").value("admin"))
                .andExpect(jsonPath("$.data.role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.data.use").value(true));
    }

    @DisplayName("로그인 하지 않고, 사용자 정보 조회 시도 시, 예외가 발생한다.")
    @Test
    void myInfoNoLogin() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/user/member")
                )
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("E007"));
    }
}