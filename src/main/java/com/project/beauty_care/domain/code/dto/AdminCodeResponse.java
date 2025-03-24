package com.project.beauty_care.domain.code.dto;

import com.project.beauty_care.domain.dto.BaseTimeDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
public class AdminCodeResponse extends BaseTimeDto {
    private String id;
    @Setter
    private List<AdminCodeResponse> children = new ArrayList<>();
    private String name;
    private String description;
    private Integer sortNumber;
    private Boolean isUse;
}
