package com.project.beauty_care.domain.member.dto;

import com.project.beauty_care.global.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberResponse {
    private Long id;
    private String loginId;
    private String name;
    private String role;
    private boolean isUse;
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
