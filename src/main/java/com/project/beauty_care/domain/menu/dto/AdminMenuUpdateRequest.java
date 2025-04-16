package com.project.beauty_care.domain.menu.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class AdminMenuUpdateRequest extends MenuBaseRequest {
    private Long menuId;
}
