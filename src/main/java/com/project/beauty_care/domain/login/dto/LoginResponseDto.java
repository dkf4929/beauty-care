package com.project.beauty_care.domain.login.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsIn....")
    private String accessToken;

    @Schema(description = "토큰 만료시간", example = "1725872823038")
    private Long expiresIn;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    public static LoginResponseDto of(String accessToken,
                                      long expiresIn,
                                      String tokenType) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();

        loginResponseDto.accessToken = accessToken;
        loginResponseDto.expiresIn = expiresIn;
        loginResponseDto.tokenType = tokenType;

        return loginResponseDto;
    }
}
