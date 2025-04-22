package com.project.beauty_care.global.security.jwt.service;

import com.project.beauty_care.domain.menu.dto.UserMenuResponse;
import com.project.beauty_care.domain.menu.service.MenuService;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.RoleConverter;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.service.RoleService;
import com.project.beauty_care.global.enums.RedisCacheKey;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final RoleService roleService;
    private final MenuService menuService;
    private final RoleConverter roleConverter;

    public boolean checkAuthority(String authority) {
        return roleService.existsByAuthority(authority);
    }

    @Cacheable(value = RedisCacheKey.ROLE, key = "#p0", cacheManager = "redisCacheManager")
    public RoleResponse findRoleByAuthority(String authority) {
        Role role = roleService.findRoleByAuthority(authority);

        return roleConverter.toResponse(role, RoleResponse.patternMapToList(role.getUrlPatterns()));
    }


    @Cacheable(value = RedisCacheKey.MENU_ROLE, key = "#p0", cacheManager = "redisCacheManager")
    public UserMenuResponse findMyMenu(String authority) {
        return menuService.findMenuByAuthority(authority);
    }
}
