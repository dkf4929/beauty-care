package com.project.beauty_care.domain.menuRole;

import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.role.Role;
import org.springframework.stereotype.Component;

@Component
public class MenuRoleConverter {
    public MenuRole buildEntity(Menu entity, Role role) {
        return MenuRole.builder()
                .menu(entity)
                .role(role)
                .build();
    }
}
