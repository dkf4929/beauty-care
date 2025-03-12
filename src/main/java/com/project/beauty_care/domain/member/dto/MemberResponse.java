package com.project.beauty_care.domain.member.dto;

import com.project.beauty_care.domain.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberResponse extends BaseDto {
    private Long id;
    private String loginId;
    private String name;
    private String role;
    private boolean isUse;
    private LocalDateTime lastLoginDateTime;
}
