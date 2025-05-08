package com.project.beauty_care.domain.code;

import com.project.beauty_care.domain.code.dto.AdminCodeCreateRequest;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.mapper.CodeMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CodeConverter {
    public CodeResponse toHierarchy(Code code) {
        CodeResponse dto = CodeMapper.INSTANCE.toResponse(code);

        List<CodeResponse> childrenList = code.getChildren().stream()
                .map(CodeMapper.INSTANCE::toResponse)
                .toList();

        dto.setChildren(childrenList);
        return dto;
    }

    public CodeResponse toResponse(Code code) {
        return CodeMapper.INSTANCE.toResponse(code);
    }

    public CodeResponse toResponse(Code code, List<CodeResponse> children) {
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
