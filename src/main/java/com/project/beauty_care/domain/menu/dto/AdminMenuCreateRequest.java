package com.project.beauty_care.domain.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class AdminMenuCreateRequest extends MenuBaseRequest {
    @Schema(example = "1")
    private Long parentMenuId;
}
