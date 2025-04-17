package com.project.beauty_care.domain.code.service;

import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.CodeConverter;
import com.project.beauty_care.domain.code.CodeValidator;
import com.project.beauty_care.domain.code.dto.AdminCodeCreateRequest;
import com.project.beauty_care.domain.code.dto.AdminCodeResponse;
import com.project.beauty_care.domain.code.dto.AdminCodeUpdateRequest;
import com.project.beauty_care.domain.code.repository.CodeRepository;
import com.project.beauty_care.domain.mapper.CodeMapper;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.RedisCacheKey;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.RequestInvalidException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CodeService {
    private final CodeRepository repository;
    private static final String KEY = "all";
    private final CodeValidator validator;
    private final CodeConverter converter;

    // 조회 ALL
    @Cacheable(value = RedisCacheKey.CODE, key = "'all'", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public AdminCodeResponse findAllCode() {
        Optional<Code> codeOptional = repository.findByParentIsNull();

        if (codeOptional.isPresent()) {
            Code entity = codeOptional.get();
            List<AdminCodeResponse> childList = entity.getChildren().stream()
                    .map(converter::toHierarchy)
                    .toList();

            return converter.toResponse(entity, childList);
        } else
            return AdminCodeResponse.builder().build();
    }

    // 조회 BY ID
    @Cacheable(value = RedisCacheKey.CODE, key = "#p0", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public AdminCodeResponse findCodeById(String codeId) {
        Code entity = findById(codeId);
        return converter.toResponse(entity);
    }

    @CacheEvict(value = RedisCacheKey.CODE, allEntries = true, cacheManager = "redisCacheManager")
    public AdminCodeResponse createCode(AdminCodeCreateRequest request) {
        Code parent = null;
        checkExistsId(request.getCodeId());

        if (ObjectUtils.isNotEmpty(request.getParentId()))
            parent = repository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(Errors.INTERNAL_SERVER_ERROR));

        Code entity = converter.buildEntity(request, parent);

        return converter.toResponse(repository.save(entity));
    }

    @CacheEvict(value = RedisCacheKey.CODE, allEntries = true, cacheManager = "redisCacheManager")
    public AdminCodeResponse updateCode(String codeId, AdminCodeUpdateRequest request) {
        Code entity = findById(codeId);

        if (!request.getIsUse()) entity.getChildren().forEach(this::updateIsUseFalse);

        return converter.toResponse(entity.update(request));
    }

    @CacheEvict(value = RedisCacheKey.CODE, allEntries = true, cacheManager = "redisCacheManager")
    public String deleteCode(String codeId) {
        Code entity = findById(codeId);

        validator.checkIsDeletable(entity);
        repository.deleteById(codeId);

        return codeId;
    }

    private Code findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_CODE));
    }

    private void updateIsUseFalse(Code entity) {
        entity.updateIsUse(Boolean.FALSE);
        entity.getChildren().forEach(this::updateIsUseFalse);
    }

    private void checkExistsId(String id) {
        if(repository.existsById(id)) throw new RequestInvalidException(Errors.DUPLICATED_CODE);
    }
}
