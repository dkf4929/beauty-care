package com.project.beauty_care.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminMemberUpdateRequest {
    @NotNull(message = "사용자 ID를 입력하세요.")
    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "권한", example = "USER")
    private String role;

    @NotNull(message = "계정 사용 여부를 입력하세요.")
    @Schema(description = "계정 사용 여부", example = "true")
    private Boolean isUse;

    @Builder
    public AdminMemberUpdateRequest(Long id, String role, Boolean isUse) {
        this.id = id;
        this.role = role;
        this.isUse = isUse;
    }
}
