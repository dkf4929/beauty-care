package com.project.beauty_care.domain.menuRole.service;

import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menuRole.MenuRole;
import com.project.beauty_care.domain.menuRole.repository.MenuRoleRepository;
import com.project.beauty_care.domain.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuRoleService {
    private final MenuRoleRepository repository;

    public List<MenuRole> createMenuRoleWithMenuAndRole(Menu menu, List<Role> roles) {
        return roles.stream()
                .map(role -> buildMenuRole(menu, role))
                .toList();
    }

    public void saveAllMenuRoles(List<MenuRole> menuRoles) {
        repository.saveAll(menuRoles);
    }

    private MenuRole buildMenuRole(Menu entity, Role role) {
        return MenuRole.builder()
                .menu(entity)
                .role(role)
                .build();
    }
}
