package com.project.beauty_care.domain.role;

import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menuRole.MenuRole;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleConverter {
    public List<RoleResponse> toResponseWithMenu(Menu menu) {
        return menu.getMenuRole().stream()
                .map(MenuRole::getRole)
                .map(role -> RoleMapper.INSTANCE.toResponse(role, RoleResponse.patternMapToList(role.getUrlPatterns())))
                .toList();
    }
}
