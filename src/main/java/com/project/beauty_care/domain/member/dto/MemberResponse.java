package com.project.beauty_care.domain.member.dto;

import com.project.beauty_care.domain.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberResponse extends BaseDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "로그인 ID", example = "user1")
    private String loginId;

    @Schema(description = "사용자명", example = "홍길동")
    private String name;

    @Schema(description = "권한", example = "ROLE_USER")
    private String role;

    @Schema(description = "계정 잠금 여부", example = "false")
    private Boolean isUse;

    @Schema(description = "마지막 로그인 시간", example = "2025-01-01 00:00:00")
    private LocalDateTime lastLoginDateTime;

    @Builder
    public MemberResponse(Long id, String loginId, String name, String role, boolean isUse, LocalDateTime lastLoginDateTime) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.role = role;
        this.isUse = isUse;
        this.lastLoginDateTime = lastLoginDateTime;
    }
}
