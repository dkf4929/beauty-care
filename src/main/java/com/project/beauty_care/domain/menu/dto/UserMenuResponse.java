package com.project.beauty_care.domain.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserMenuResponse {
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
    private List<UserMenuResponse> children = new ArrayList<>();
}
