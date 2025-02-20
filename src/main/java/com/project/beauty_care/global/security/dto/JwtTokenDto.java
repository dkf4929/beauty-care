package com.project.beauty_care.global.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtTokenDto {
    private String grantType;
    private String accessToken;
    private Long accessTokenExpiresIn;

    @Builder
    public JwtTokenDto(String grantType, String accessToken, Long accessTokenExpiresIn) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }
}
