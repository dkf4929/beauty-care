package com.project.beauty_care.domain.login;

import com.project.beauty_care.ControllerTestSupport;
import com.project.beauty_care.domain.login.dto.LoginRequest;
import com.project.beauty_care.global.enums.ErrorCodes;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.enums.SuccessResult;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.security.dto.JwtTokenDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
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
                .role(Role.ADMIN.getValue())
                .build();

        when(loginService.login(any(LoginRequest.class))).thenReturn(appUser);
        when(jwtTokenProvider.generateToken(any()))
                .thenReturn(
                        new JwtTokenDto("accessToken", "ej1234....", 100L)
                );

        // 로그인 요청 테스트
        performLogin(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(SuccessResult.LOGIN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessResult.LOGIN_SUCCESS.getCode()))
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
        ;
    }

    private ResultActions performLogin(LoginRequest request) throws Exception {
        return mockMvc.perform(
                post("/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}