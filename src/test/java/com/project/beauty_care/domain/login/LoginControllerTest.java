package com.project.beauty_care.domain.login;

import com.project.beauty_care.ControllerTestSupport;
import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.ErrorCodes;
import com.project.beauty_care.global.enums.SuccessCodes;
import com.project.beauty_care.global.login.dto.LoginRequest;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.security.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest extends ControllerTestSupport {
    @DisplayName("ID, PASSWORD를 입력해, 로그인한다.")
    @Test
    void login() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .loginId("admin")
                .password("abc123")
                .build();

        AppUser appUser = AppUser.builder()
                .loginId("admin")
                .name("admin")
                .role(RoleMapper.INSTANCE.toSimpleDto(buildRole(Authentication.ADMIN.getName())))
                .build();

        when(loginService.login(any(LoginRequest.class))).thenReturn(appUser);
        when(jwtTokenProvider.generateToken(any(), anyLong()))
                .thenReturn(
                        new LoginResponse("accessToken", "ej1234....", 100L)
                );

        // 로그인 요청 테스트
        performLogin(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successMessage").value(SuccessCodes.LOGIN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.successCode").value(SuccessCodes.LOGIN_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @DisplayName("로그인할 때 아이디, 비밀번호 필수 입력")
    @Test
    void loginWithoutRequest() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .build();

        performLogin(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("비밀번호는 필수입니다")))
                .andExpect(jsonPath("$.message", containsString("ID는 필수입니다")))
                .andExpect(jsonPath("$.code").value(ErrorCodes.API_REQUEST_INVALID_VALUE.getErrorCode()));
    }

    private ResultActions performLogin(LoginRequest request) throws Exception {
        return mockMvc.perform(
                post("/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private Role buildRole(String role) {
        return Role.builder()
                .roleName(role)
                .urlPatterns(Collections.emptyMap())
                .build();
    }
}