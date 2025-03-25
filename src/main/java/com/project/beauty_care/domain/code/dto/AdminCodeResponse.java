package com.project.beauty_care.domain.code.dto;

import com.project.beauty_care.domain.dto.BaseTimeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
