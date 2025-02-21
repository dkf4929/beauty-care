package com.project.beauty_care.domain.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class LoginRequestDto {
    @NotNull
    @Schema(description = "로그인 ID", example = "admin")
    private String loginId;

    @NotNull
    @Schema(description = "비밀번호", example = "qwer1234")
    private String password;

    @Builder
    public LoginRequestDto(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
