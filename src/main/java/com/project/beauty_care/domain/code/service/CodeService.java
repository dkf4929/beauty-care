package com.project.beauty_care.domain.code.service;

import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.dto.AdminCodeCreateRequest;
import com.project.beauty_care.domain.code.dto.AdminCodeResponse;
import com.project.beauty_care.domain.code.dto.AdminCodeUpdateRequest;
import com.project.beauty_care.domain.code.repository.CodeRepository;
import com.project.beauty_care.domain.mapper.CodeMapper;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.RedisCacheKey;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.RequestInvalidException;
import com.project.beauty_care.global.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CodeService {
    private final CodeRepository repository;
    private final CommonUtils utils;

    // 조회 ALL
    @Cacheable(value = RedisCacheKey.ALL_CODES, cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public AdminCodeResponse findAllCode() {
        // 최상위 코드 검색
        Optional<Code> codeOptional = repository.findByParentIsNull();

        if (codeOptional.isPresent()) {
            Code entity = codeOptional.get();

            List<AdminCodeResponse> childList = entity.getChildren().stream()
                    .map(this::toDto)
                    .toList();

            return CodeMapper.INSTANCE.toAdminCodeResponse(entity, childList);
        } else
            return AdminCodeResponse.builder().build();
    }

    // 조회 BY ID
    @Cacheable(value = RedisCacheKey.ALL_CODES, key = "#p0", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public AdminCodeResponse findCodeById(String codeId) {
        Code entity = findById(codeId);

        return CodeMapper.INSTANCE.toDto(entity);
    }

    public AdminCodeResponse createCode(AdminCodeCreateRequest request) {
        Code parent = null;

        // 동일한 ID가 존재하는지 확인
        checkExistsId(request.getCodeId());

        if (ObjectUtils.isNotEmpty(request.getParentId()))
            parent = repository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(Errors.INTERNAL_SERVER_ERROR));

        Code entity = Code.builder()
                .id(request.getCodeId())
                .name(request.getName())
                .sortNumber(request.getSortNumber())
                .description(request.getDescription())
                .isUse(request.getIsUse())
                .parent(parent)
                .build();

        Code savedEntity = repository.save(entity);

        // redis cache clear
        utils.clearRedisCache(RedisCacheKey.ALL_CODES);

        return CodeMapper.INSTANCE.toDto(savedEntity);
    }

    // 수정
    public AdminCodeResponse updateCode(String codeId, AdminCodeUpdateRequest request) {
        Code entity = findById(codeId);

        // 하위코드가 존재할 경우 -> 하위코드들의 isUse를 false로
        if (!request.getIsUse()) entity.getChildren().forEach(this::updateIsUseFalse);

        Code updatedEntity = entity.update(request);

        // redis cache clear
        utils.clearRedisCache(RedisCacheKey.ALL_CODES, RedisCacheKey.CODE + updatedEntity.getId());

        return CodeMapper.INSTANCE.toDto(updatedEntity);
    }

    // 삭제
    public String deleteCode(String codeId) {
        Code entity = findById(codeId);

        // 하위코드가 존재하는 경우, 삭제 불가
        checkIsDeletable(entity);
        repository.deleteById(codeId);

        // redis cache clear
        utils.clearRedisCache(RedisCacheKey.ALL_CODES, RedisCacheKey.CODE + codeId);

        return codeId;
    }

    private Code findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_CODE));
    }

    private void checkIsDeletable(Code entity) {
        if (!entity.getChildren().isEmpty()) throw new RequestInvalidException(Errors.CAN_NOT_DELETE_CODE);
    }

    private AdminCodeResponse toDto(Code code) {
        AdminCodeResponse dto = CodeMapper.INSTANCE.toDto(code);

        List<AdminCodeResponse> childrenList = code.getChildren().stream()
                .map(CodeMapper.INSTANCE::toDto)
                .toList();

        dto.setChildren(childrenList);

        return dto;
    }

    // softDelete
    private void updateIsUseFalse(Code entity) {
        entity.updateIsUse(Boolean.FALSE);

        entity.getChildren()
                .forEach(this::updateIsUseFalse);
    }

    private void checkExistsId(String id) {
        if(repository.existsById(id)) throw new RequestInvalidException(Errors.DUPLICATED_CODE);
    }
}
