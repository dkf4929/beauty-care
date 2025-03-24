package com.project.beauty_care.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class BaseDto extends BaseTimeDto {
    @Schema(description = "생성자 ID", example = "1")
    private Long createdBy;

    @Schema(description = "수정자 ID", example = "1")
    private Long updatedBy;
}
