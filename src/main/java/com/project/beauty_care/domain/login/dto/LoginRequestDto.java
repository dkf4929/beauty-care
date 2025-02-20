package com.project.beauty_care.domain.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class LoginRequestDto {
    @NotBlank
    @Schema(description = "로그인 ID", example = "admin")
    private String loginId;

    @NotBlank
    @Schema(description = "비밀번호", example = "qwer1234")
    private String password;
}
