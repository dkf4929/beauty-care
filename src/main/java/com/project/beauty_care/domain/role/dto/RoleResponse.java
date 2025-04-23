package com.project.beauty_care.domain.role.dto;

import com.project.beauty_care.domain.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
public class RoleResponse extends BaseDto {
    @Schema(description = "권한명", example = "ADMIN")
    private String roleName;

    @Schema(description = "허용할 URL 패턴", examples = {"/admin/**", "/user/**"})
    private List<String> urlPatterns = new ArrayList<>();

    @Schema(description = "사용 여부", example = "true")
    private Boolean isUse;

    public static List<String> patternMapToList(Map<String, Object> urlPatterns) {
        if (urlPatterns == null) return new ArrayList<>();

        return Optional.ofNullable(urlPatterns.getOrDefault("pattern", new ArrayList<>()))
                .filter(List.class::isInstance)
                .map(list -> (List<?>) list)
                .orElse(Collections.emptyList())
                .stream()
                .map(String::valueOf)
                .toList();
    }
}
