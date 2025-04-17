package com.project.beauty_care.domain.code;

import com.project.beauty_care.domain.code.dto.AdminCodeCreateRequest;
import com.project.beauty_care.domain.code.dto.AdminCodeResponse;
import com.project.beauty_care.domain.mapper.CodeMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CodeConverter {
    public AdminCodeResponse toHierarchy(Code code) {
        AdminCodeResponse dto = CodeMapper.INSTANCE.toResponse(code);

        List<AdminCodeResponse> childrenList = code.getChildren().stream()
                .map(CodeMapper.INSTANCE::toResponse)
                .toList();

        dto.setChildren(childrenList);
        return dto;
    }

    public AdminCodeResponse toResponse(Code code) {
        return CodeMapper.INSTANCE.toResponse(code);
    }

    public AdminCodeResponse toResponse(Code code, List<AdminCodeResponse> children) {
        return CodeMapper.INSTANCE.toResponse(code, children);
    }

    public Code buildEntity(AdminCodeCreateRequest request, Code parent) {
        return Code.builder()
                .id(request.getCodeId())
                .name(request.getName())
                .sortNumber(request.getSortNumber())
                .description(request.getDescription())
                .isUse(request.getIsUse())
                .parent(parent)
                .build();
    }
}
