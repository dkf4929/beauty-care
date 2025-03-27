package com.project.beauty_care.domain.role.service;

import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.NoAuthorityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public List<Role> findRolesByUrlPattern(String url) {
        return repository.findAll()
                .stream()
                .filter(role -> {
                    Object patternObject = role.getUrlPatterns()
                            .getOrDefault("pattern", Collections.emptyList());

                    if (patternObject instanceof List<?> patternList)
                        return patternList.stream()
                                .map(String::valueOf)
                                .anyMatch(pattern -> matcher.match(pattern, url) || pattern.equals(url));

                    return false;
                })
                .toList();
    }

    public void checkAuthority(String authority) {
        repository.findAll().stream()
                .filter(role -> role.getRoleName().equals(authority))
                .findFirst()
                .orElseThrow(() -> new NoAuthorityMember(Errors.NO_AUTHORITY_MEMBER));
    }

    public Role findRoleByAuthority(String authority) {
        return findById(authority);
    }

    private Role findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_ROLE));
    }
}
