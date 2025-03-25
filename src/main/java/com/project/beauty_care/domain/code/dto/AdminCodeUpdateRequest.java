package com.project.beauty_care.domain.code.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminCodeUpdateRequest {
    @NotBlank(message = "코드명을 입력하세요.")
    @Schema(description = "코드명", example = "시스템")
    private String name;

    @Schema(description = "설명", example = "시스템(최상위 코드)")
    private String description;

    @NotNull(message = "정렬 순서를 입력하세요.")
    @Schema(description = "정렬 순서", example = "1")
    private Integer sortNumber;

    @NotNull(message = "코드 사용 여부를 입력하세요.")
    @Schema(description = "코드 사용 여부", example = "true")
    private Boolean isUse;

    @Builder
    public AdminCodeUpdateRequest(String name, String description, Integer sortNumber, Boolean isUse) {
        this.name = name;
        this.description = description;
        this.sortNumber = sortNumber;
        this.isUse = isUse;
    }
}
