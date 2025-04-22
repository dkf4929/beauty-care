package com.project.beauty_care.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.beauty_care.domain.dto.BaseDto;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class AdminMenuResponse extends BaseDto {
    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

    @Schema(description = "메뉴명", example = "시스템")
    private String menuName;

    @Schema(description = "메뉴 경로", example = "/system")
    private String menuPath;

    @Schema(description = "정렬 순서", example = "0")
    private Integer sortNumber;

    @Schema(description = "최하위 메뉴 여부", example = "false")
    private Boolean isLeaf;

    @Setter
    @Schema(description = "하위 메뉴")
    private List<AdminMenuResponse> children = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<RoleResponse> roles = new ArrayList<>();

    @Schema(description = "사용 여부", example = "true")
    private Boolean isUse;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AdminMenuResponse that = (AdminMenuResponse) object;
        return Objects.equals(menuId, that.menuId) &&
                Objects.equals(menuName, that.menuName) &&
                Objects.equals(menuPath, that.menuPath) &&
                Objects.equals(sortNumber, that.sortNumber) &&
                Objects.equals(isLeaf, that.isLeaf) &&
                Objects.equals(children, that.children) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(isUse, that.isUse);
    }
}
