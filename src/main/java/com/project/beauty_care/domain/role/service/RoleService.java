package com.project.beauty_care.domain.role.service;

import com.project.beauty_care.domain.member.MemberConverter;
import com.project.beauty_care.domain.member.dto.MemberSummaryResponse;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.RoleConverter;
import com.project.beauty_care.domain.role.dto.RoleCreateRequest;
import com.project.beauty_care.domain.role.dto.RoleMemberResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.dto.RoleUpdateRequest;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.RedisCacheKey;
import com.project.beauty_care.global.exception.CustomException;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;
    private final AntPathMatcher matcher = new AntPathMatcher();
    private static final String PATTERN = "pattern";
    private final RoleConverter converter;
    private final MemberConverter memberConverter;

    @Transactional(readOnly = true)
    @Cacheable(value = RedisCacheKey.ROLE, key = "'all'", cacheManager = "redisCacheManager")
    public List<RoleMemberResponse> findAllRoles(String roleName) {
        List<Role> roles = StringUtils.hasText(roleName) ? repository.findAllByRoleName(roleName) : repository.findAll();

        // toDto
        return roles.stream()
                .map(role -> {
                    List<MemberSummaryResponse> summaryResponseList
                            = memberConverter.toSummaryResponse(role.getMembers());

                    return converter.toResponse(
                            role,
                            RoleResponse.patternMapToList(role.getUrlPatterns()),
                            summaryResponseList
                    );
                })
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheKey.ROLE, allEntries = true, cacheManager = "redisCacheManager"),
            @CacheEvict(value = RedisCacheKey.ROLE_EXISTS, allEntries = true, cacheManager = "redisCacheManager")
    })
    public RoleResponse createRole(RoleCreateRequest request) {
        // 동일한 ID가 존재하는지 확인
        checkExistsRoleName(request.getRoleName());

        // LIST to JSON
        Map<String, Object> patternMap = Map.of(PATTERN, request.getUrlPatterns());

        // build entity
        Role entity = converter.buildEntity(request, patternMap);

        Role savedEntity = repository.save(entity);

        return converter.toResponse(savedEntity, RoleResponse.patternMapToList(savedEntity.getUrlPatterns()));
    }

    @Transactional(readOnly = true)
    public Role checkAuthority(String authority) {
        return findById(authority);
    }

    @Transactional(readOnly = true)
    public Role findRoleByAuthority(String authority) {
        return findById(authority);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheKey.ROLE, allEntries = true, cacheManager = "redisCacheManager"),
            @CacheEvict(value = RedisCacheKey.ROLE_EXISTS, allEntries = true, cacheManager = "redisCacheManager")
    })
    public RoleResponse updateRole(RoleUpdateRequest request) {
        if (!request.getBeforeRoleName().equals(request.getAfterRoleName()))
            checkExistsRoleName(request.getAfterRoleName());

        Role entity = findById(request.getBeforeRoleName());

        Map<String, Object> patternMap = Map.of(PATTERN, request.getUrlPatterns());

        entity.updateRole(request, patternMap);

        return converter.toResponse(entity, RoleResponse.patternMapToList(entity.getUrlPatterns()));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheKey.ROLE, allEntries = true, cacheManager = "redisCacheManager"),
            @CacheEvict(value = RedisCacheKey.ROLE_EXISTS, allEntries = true, cacheManager = "redisCacheManager")
    })
    public void hardDeleteRole(String role) {
        repository.deleteById(role);
    }

    private Role findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_ROLE));
    }

    public List<Role> findRoleByRoleNames(List<String> roleNames) {
        return repository.findAllById(roleNames);
    }

    @Cacheable(value = RedisCacheKey.ROLE_EXISTS, key = "#p0", cacheManager = "redisCacheManager")
    public boolean existsByAuthority(String authority) {
        return repository.existsByRoleNameAndIsUseIsTrue(authority);
    }

    private boolean isPatternMatch(String url, Role role) {
        return Optional.ofNullable(role.getUrlPatterns().get(PATTERN))
                .filter(List.class::isInstance)
                .map(list -> (List<?>) list)
                .orElse(Collections.emptyList())
                .stream()
                .map(String::valueOf)
                .anyMatch(pattern -> matcher.match(pattern, url) || pattern.equals(url));
    }

    private void checkExistsRoleName(String roleName) {
        if (repository.existsById(roleName)) throw new CustomException(Errors.DUPLICATED_ROLE);
    }
}
