package com.project.beauty_care.domain.code.dto;

import com.project.beauty_care.domain.dto.BaseTimeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@SuperBuilder
public class AdminCodeResponse extends BaseTimeDto {
    @Schema(description = "코드 ID", example = "sys:agree:Y")
    private String id;

    @Setter
    @Schema(description = "하위 코드")
    private List<AdminCodeResponse> children = new ArrayList<>();

    @Schema(description = "코드명", example = "동의")
    private String name;

    @Schema(description = "설명", example = "동의")
    private String description;

    @Schema(description = "정렬 순서", example = "1")
    private Integer sortNumber;

    @Schema(description = "사용 여부", example = "true")
    private Boolean isUse;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AdminCodeResponse that = (AdminCodeResponse) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(sortNumber, that.sortNumber) &&
                Objects.equals(isUse, that.isUse) &&
                Objects.equals(children, that.children);
    }
}
