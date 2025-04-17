package com.project.beauty_care.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class AdminMenuResponse extends MenuResponse {
    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<RoleResponse> roles = new ArrayList<>();

    @Schema(description = "사용 여부", example = "true")
    private Boolean isUse;
}
