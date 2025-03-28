package com.project.beauty_care.domain.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RoleCreateRequest {
    @NotBlank(message = "권한명을 입력하세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]+(?:_[a-zA-Z0-9]+)*$",
            message = "권한명은 영문, 숫자, '_'만 사용할 수 있으며, '_'로 시작할 수 없습니다.")
    @Schema(description = "권한명", example = "ADMIN")
    private String roleName;

    @NotEmpty(message = "패턴을 입력하세요.")
    @Schema(description = "인가 패턴", example = "[\"/admin/**\", \"/user/**\"]")
    private List<String> urlPatterns = new ArrayList<>();

    @Schema(description = "권한 사용여부", example = "true")
    private Boolean isUse;
}
