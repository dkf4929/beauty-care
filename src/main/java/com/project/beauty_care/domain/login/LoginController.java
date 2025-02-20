package com.project.beauty_care.domain.login;

import com.project.beauty_care.domain.login.dto.LoginRequestDto;
import com.project.beauty_care.domain.login.dto.LoginResponseDto;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.global.enums.SuccessResult;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.security.dto.JwtTokenDto;
import com.project.beauty_care.global.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "LOGIN REST API", description = "로그인 API")
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
                            schema = @Schema(implementation = LoginResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"result\": \"1\", \"code\": \"4002\",\"message\": \"올바른 입력값을 입력하세요.\" }"))),
            @ApiResponse(responseCode = "401", description = "PASSWORD MISS MATCH", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"result\": \"1\", \"code\": \"1002\", \"message\": \"비밀번호가 일치하지 않습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "ENTITY NOT FOUND", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"result\": \"1\", \"code\": \"2001\", \"message\": \"등록된 회원이 아닙니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"result\": \"1\", \"code\": \"9999\", \"message\": \"INTERNAL SERVER ERROR\" }"))),
    })
    @PostMapping
    public com.project.beauty_care.global.ApiResponse<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        // 로그인 인증 처리
        Member loginMember = service.login(loginRequestDto);

        // USER 인증 dto
        AppUser appUser = AppUser.builder()
                .memberId(loginMember.getId())
                .loginId(loginMember.getLoginId())
                .name(loginMember.getName())
                .role(loginMember.getRole())
                .build();

        // 인증 객체 생성
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(appUser, appUser.getLoginId(), appUser.getAuthorities());

        // accessToken 및 refreshToken 생성
        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(authentication);

        LoginResponseDto loginResponseDto = LoginResponseDto.of(jwtTokenDto.getAccessToken(),
                jwtTokenDto.getAccessTokenExpiresIn(),
                JwtTokenProvider.TOKEN_TYPE);

        return com.project.beauty_care.global.ApiResponse.success(
                SuccessResult.LOGIN_SUCCESS,
                HttpStatus.OK,
                loginResponseDto
        );
    }
}
