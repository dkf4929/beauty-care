package com.project.beauty_care.domain.dto;

import lombok.*;

@Getter
@Setter
public abstract class BaseDto extends BaseTimeDto {
    private Long createdBy;
    private Long updatedBy;
}
