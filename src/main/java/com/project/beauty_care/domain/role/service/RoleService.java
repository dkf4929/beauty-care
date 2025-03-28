package com.project.beauty_care.domain.role.service;

import com.project.beauty_care.domain.mapper.MemberMapper;
import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.MemberSummaryResponse;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.dto.RoleCreateRequest;
import com.project.beauty_care.domain.role.dto.RoleMemberResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.dto.RoleUpdateRequest;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.CustomException;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.NoAuthorityMember;
import com.project.beauty_care.global.exception.RequestInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    private final RoleRepository repository;
    private final AntPathMatcher matcher = new AntPathMatcher();
    private static final String PATTERN = "pattern";

    @Transactional(readOnly = true)
    public List<RoleMemberResponse> findAllRoles(String roleName) {
        List<Role> roles = StringUtils.hasText(roleName) ? repository.findAllByRoleName(roleName) : repository.findAll();

        // toDto
        return roles.stream()
                .map(role -> {
                    List<MemberSummaryResponse> summaryResponseList = memberToSummaryDto(role.getMembers());

                    return RoleMapper.INSTANCE.toDto(role, patternMaptoList(role.getUrlPatterns()), summaryResponseList);
                })
                .toList();
    }

    public RoleResponse createRole(RoleCreateRequest request) {
        // 동일한 ID가 존재하는지 확인
        checkExistsRoleName(request.getRoleName());

        // LIST to JSON
        Map<String, Object> patternMap = Map.of(PATTERN, request.getUrlPatterns());

        // build entity
        Role entity = buildEntity(request, patternMap);

        Role savedEntity = repository.save(entity);

        List<String> patterns = patternMaptoList(savedEntity.getUrlPatterns());

        return RoleMapper.INSTANCE.toDto(savedEntity, patterns);
    }

    // 캐시 처리 필요.. (redis 사용 시)
    @Transactional(readOnly = true)
    public List<Role> findRolesByUrlPattern(String url) {
        return repository.findAllByIsUseIsTrue()
                .stream()
                .filter(role -> isPatternMatch(url, role))
                .toList();
    }

    @Transactional(readOnly = true)
    public void checkAuthority(String authority) {
        repository.findAll().stream()
                .filter(role -> role.getRoleName().equals(authority))
                .findFirst()
                .orElseThrow(() -> new NoAuthorityMember(Errors.NO_AUTHORITY_MEMBER));
    }

    @Transactional(readOnly = true)
    public Role findRoleByAuthority(String authority) {
        return findById(authority);
    }

    public RoleResponse updateRole(RoleUpdateRequest request) {
        if (!request.getBeforeRoleName().equals(request.getAfterRoleName()))
            checkExistsRoleName(request.getAfterRoleName());

        Role entity = findById(request.getBeforeRoleName());

        Map<String, Object> patternMap = Map.of(PATTERN, request.getUrlPatterns());

        entity.updateRole(request, patternMap);

        return RoleMapper.INSTANCE.toDto(entity, patternMaptoList(entity.getUrlPatterns()));
    }

    public void hardDeleteRole(String role) {
        repository.deleteById(role);
    }

    private Role findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_ROLE));
    }

    private Role buildEntity(RoleCreateRequest request, Map<String, Object> patternMap) {
        return Role.builder()
                .roleName(request.getRoleName().toUpperCase())
                .urlPatterns(patternMap)
                .isUse(request.getIsUse())
                .build();
    }

    private List<MemberSummaryResponse> memberToSummaryDto(List<Member> memberList) {
        return memberList.stream()
                .map(MemberMapper.INSTANCE::toSummaryDto)
                .toList();
    }

    private List<String> patternMaptoList(Map<String, Object> urlPatterns) {
        List<String> patterns = new ArrayList<>();

        Object patternObject = urlPatterns.getOrDefault(PATTERN, Collections.emptyList());

        if (patternObject instanceof List<?> patternList)
            patterns = patternList.stream()
                    .map(String::valueOf)
                    .toList();
        return patterns;
    }

    private boolean isPatternMatch(String url, Role role) {
        Object patternObject = role.getUrlPatterns()
                .getOrDefault("pattern", Collections.emptyList());

        if (patternObject instanceof List<?> patternList)
            return patternList.stream()
                    .map(String::valueOf)
                    .anyMatch(pattern -> matcher.match(pattern, url) || pattern.equals(url));

        return false;
    }

    private void checkExistsRoleName(String roleName) {
        if (repository.existsById(roleName)) throw new CustomException(Errors.DUPLICATED_ROLE);
    }
}
