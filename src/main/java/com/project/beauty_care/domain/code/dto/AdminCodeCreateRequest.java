package com.project.beauty_care.domain.code.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class AdminCodeCreateRequest {
    @NotBlank(message = "코드 ID를 입력하세요.")
    @Pattern(regexp = "^[A-Za-z0-9:]+$", message = "ID는 영문 대소문자, 숫자, 콜론(:)만 입력 가능합니다.")
    @Length(min=1, max=25, message = "ID는 1 ~ 25글자 사이로 입력해주세요")
    @Schema(description = "코드 ID", example = "sys:agree:Y")
    private String codeId;

    @Schema(description = "상위 코드 ID", example = "sys:agree")
    private String parentId;

    @NotBlank(message = "코드명을 입력하세요.")
    @Length(min = 2, max = 20, message = "코드명은 2~20 글자 입니다.")
    @Schema(description = "코드명", example = "동의")
    private String name;

    @Schema(description = "설명", example = "동의")
    private String description;

    @NotNull(message = "정렬 순서를 입력하세요.")
    @Schema(description = "정렬 순서", example = "1")
    private Integer sortNumber;

    @NotNull(message = "코드 사용 여부를 입력하세요.")
    @Schema(description = "사용 여부", example = "true")
    private Boolean isUse;

    @Builder
    public AdminCodeCreateRequest(String codeId, String parentId, String name, String description, Integer sortNumber, Boolean isUse) {
        this.codeId = codeId;
        this.parentId = parentId;
        this.name = name;
        this.description = description;
        this.sortNumber = sortNumber;
        this.isUse = isUse;
    }
}
