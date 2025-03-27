package com.project.beauty_care.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminMemberCreateRequest {
    @NotBlank(message = "로그인 ID는 필수입니다")
    @Size(min = 4, max = 10, message = "아이디는 4~10자리의 문자 형태로 입력해야 합니다")
    @Schema(description = "로그인 아이디", example = "user")
    private String loginId;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 20, message = "이름은 2~20자리의 문자 형태로 입력해야 합니다")
    @Schema(description = "사용자명", example = "user")
    private String name;

    @Schema(description = "권한", example = "USER")
    private String role;

    @Schema(description = "계정 잠금 여부", example = "false")
    private Boolean isUse;

    @Builder
    public AdminMemberCreateRequest(String loginId, String name, String role, Boolean isUse) {
        this.loginId = loginId;
        this.name = name;
        this.role = role;
        this.isUse = isUse;
    }
}
