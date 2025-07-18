package com.project.beauty_care.domain.code.service;

import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.CodeConverter;
import com.project.beauty_care.domain.code.CodeValidator;
import com.project.beauty_care.domain.code.dto.AdminCodeCreateRequest;
import com.project.beauty_care.domain.code.dto.AdminCodeUpdateRequest;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.code.repository.CodeRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.RedisCacheKey;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.RequestInvalidException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CodeService {
    private final CodeRepository repository;
    private final CodeValidator validator;
    private final CodeConverter converter;

    // 조회 ALL
    @Cacheable(value = RedisCacheKey.CODE, key = "'all'", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public CodeResponse findAllCode() {
        Optional<Code> codeOptional = repository.findByParentIsNull();

        if (codeOptional.isPresent()) {
            Code entity = codeOptional.get();
            List<CodeResponse> childList = entity.getChildren().stream()
                    .map(converter::toHierarchy)
                    .toList();

            return converter.toResponse(entity, childList);
        } else
            return CodeResponse.builder().build();
    }

    // 조회 BY ID
    @Cacheable(value = RedisCacheKey.CODE, key = "#p0", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public CodeResponse findCodeByIdCache(String codeId) {
        Code entity = findById(codeId);
        return converter.toResponse(entity);
    }

    // 하위 코드 조회
    @Cacheable(value = RedisCacheKey.CODE_PARENT, key = "#p0", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public List<CodeResponse> findCodeByParentId(String parentId) {
        Code parent = findById(parentId);

        return parent.getChildren().stream()
                .map(converter::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Code findCodeById(String codeId) {
        return findById(codeId);
    }

    @Caching(evict = {
            @CacheEvict(value = RedisCacheKey.CODE, key = "'all'", cacheManager = "redisCacheManager"),
            @CacheEvict(value = RedisCacheKey.CODE, key = "#request.codeId", cacheManager = "redisCacheManager"),
            @CacheEvict(value = RedisCacheKey.CODE_PARENT, key = "#request.codeId", cacheManager = "redisCacheManager"),
    })
    public CodeResponse createCode(AdminCodeCreateRequest request) {
        Code parent = null;
        checkExistsId(request.getCodeId());

        if (ObjectUtils.isNotEmpty(request.getParentId()))
            parent = repository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(Errors.INTERNAL_SERVER_ERROR));

        Code entity = converter.buildEntity(request, parent);

        return converter.toResponse(repository.save(entity));
    }

    @Caching(evict = {
            @CacheEvict(value = RedisCacheKey.CODE, key = "'all'", cacheManager = "redisCacheManager"),
            @CacheEvict(value = RedisCacheKey.CODE, key = "#p0", cacheManager = "redisCacheManager"),
            @CacheEvict(value = RedisCacheKey.CODE_PARENT, key = "#p0", cacheManager = "redisCacheManager"),
    })
    public CodeResponse updateCode(String codeId, AdminCodeUpdateRequest request) {
        Code entity = findById(codeId);

        if (!request.getIsUse()) entity.getChildren().forEach(this::updateIsUseFalse);

        return converter.toResponse(entity.update(request));
    }

    @Caching(evict = {
            @CacheEvict(value = RedisCacheKey.CODE, key = "'all'", cacheManager = "redisCacheManager"),
            @CacheEvict(value = RedisCacheKey.CODE, key = "#p0", cacheManager = "redisCacheManager"),
            @CacheEvict(value = RedisCacheKey.CODE_PARENT, key = "#p0", cacheManager = "redisCacheManager"),
    })
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
