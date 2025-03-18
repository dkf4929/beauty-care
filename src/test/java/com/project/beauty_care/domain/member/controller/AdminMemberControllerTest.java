package com.project.beauty_care.domain.member.controller;

import com.project.beauty_care.ControllerTestSupport;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminMemberControllerTest extends ControllerTestSupport {
    final String RETRIEVE_SUCCESS_MESSAGE = "조회가 완료 되었습니다.";

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
}
