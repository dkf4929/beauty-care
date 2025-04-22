package com.project.beauty_care.domain.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class MenuBaseRequest {
    @NotBlank(message = "메뉴명을 입력하세요.")
    @Size(min = 2, max = 20, message = "메뉴명은 2~20 자리의 문자 형태로 입력해야 합니다.")
    @Schema(example = "시스템")
    private String menuName;

    @Size(min = 3, max = 50, message = "메뉴 경로는 3~50 자리의 문자 형태로 입력해야 합니다.")
    @NotBlank(message = "메뉴 경로를 입력하세요.")
    @Schema(example = "/system")
    private String menuPath;

    @NotNull(message = "정렬 순서를 입력하세요.")
    @Schema(example = "0")
    private Integer sortNumber;

    @NotNull(message = "최하위 메뉴 여부를 입력하세요.")
    @Schema(example = "false")
    private Boolean isLeaf;

    @Schema(example = "최상위 메뉴")
    private String description;

    @NotNull(message = "메뉴 사용여부를 입력하세요.")
    @Schema(example = "true")
    private Boolean isUse;

    @Schema(example = "ADMIN")
    private List<String> roleNames = new ArrayList<>();

    @NotNull
    @Min(0)
    @Max(2)
    @Schema(example = "0")
    private Integer menuLevel;
}
