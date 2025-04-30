package com.project.beauty_care.global.login.controller;

import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import com.project.beauty_care.global.login.dto.LoginRequest;
import com.project.beauty_care.global.login.service.LoginService;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.security.dto.LoginResponse;
import com.project.beauty_care.global.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@Tag(name = "LOGIN REST API FOR PUBLIC", description = "로그인 API")
@RequestMapping("/login")
public class LoginController {
    private final LoginService service;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "로그인", description = "아이디와 패스워드를 입력하여 로그인 합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "401", description = "로그인 인증 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\",\"message\": \"로그인 후 진행하세요.\" }"))),
            @ApiResponse(responseCode = "404", description = "ENTITY NOT FOUND", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\", \"message\": \"등록된 사용자가 아닙니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @PostMapping
    public SuccessResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 로그인 인증 처리
        AppUser appUser = service.login(loginRequest);

        // 인증 객체 생성
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(appUser, appUser.getLoginId(), appUser.getAuthorities());

        // accessToken 및 refreshToken 생성
        LoginResponse loginResponse =
                jwtTokenProvider.generateToken(authentication, (new Date()).getTime());

        return SuccessResponse.success(SuccessCodes.LOGIN_SUCCESS, loginResponse);
    }
}
