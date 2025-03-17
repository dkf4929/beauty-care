package com.project.beauty_care.global.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "ID는 필수입니다")
    @Schema(description = "로그인 ID", example = "admin")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "비밀번호", example = "qwer1234")
    private String password;

    @Builder
    public LoginRequest(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
