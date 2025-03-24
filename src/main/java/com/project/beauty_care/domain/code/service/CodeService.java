package com.project.beauty_care.domain.code.service;

import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.dto.AdminCodeResponse;
import com.project.beauty_care.domain.mapper.CodeMapper;
import com.project.beauty_care.domain.code.repository.CodeRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodeService {
    private final CodeRepository repository;

    public AdminCodeResponse findAllCode() {
        // 최상위 코드 검색
        Code code = repository.findByParentIsNull()
                .orElseThrow(() -> new SystemException(Errors.INTERNAL_SERVER_ERROR));

        List<AdminCodeResponse> childList = code.getChildren().stream()
                .map(this::toDto)
                .toList();

        return CodeMapper.INSTANCE.toAdminCodeResponse(code, childList);
    }

    private AdminCodeResponse toDto(Code code) {
        AdminCodeResponse dto = CodeMapper.INSTANCE.toDto(code);

        List<AdminCodeResponse> childrenList = code.getChildren().stream()
                .map(CodeMapper.INSTANCE::toDto)
                .toList();

        dto.setChildren(childrenList);

        return dto;
    }
}
