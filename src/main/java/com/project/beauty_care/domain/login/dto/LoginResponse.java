package com.project.beauty_care.domain.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginResponse {
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsIn....")
    private String accessToken;

    @Schema(description = "토큰 만료시간", example = "1725872823038")
    private Long expiresIn;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    public static LoginResponse of(String accessToken,
                                   long expiresIn,
                                   String tokenType) {
        LoginResponse loginResponse = new LoginResponse();

        loginResponse.accessToken = accessToken;
        loginResponse.expiresIn = expiresIn;
        loginResponse.tokenType = tokenType;

        return loginResponse;
    }
}
