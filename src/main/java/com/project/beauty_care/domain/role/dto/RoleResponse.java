package com.project.beauty_care.domain.role.dto;

import com.project.beauty_care.domain.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleResponse extends BaseDto {
    @Schema(description = "권한명", example = "ADMIN")
    private String roleName;

    @Schema(description = "허용할 URL 패턴", examples = {"/admin/**", "/user/**"})
    private List<String> urlPatterns = new ArrayList<>();

    @Schema(description = "사용 여부", example = "true")
    private Boolean isUse;
}
