package com.project.beauty_care.global.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "로그인 응답")
public class LoginResponse {
    @Schema(description = "토큰 타입", example = "Bearer")
    private String grantType;

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsIn....")
    private String accessToken;

    @Schema(description = "토큰 만료시간", example = "1725872823038")
    private Long accessTokenExpiresIn;

    @Builder
    public LoginResponse(String grantType, String accessToken, Long accessTokenExpiresIn) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }
}
