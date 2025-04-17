package com.project.beauty_care.domain.menuRole.service;

import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menuRole.MenuRole;
import com.project.beauty_care.domain.menuRole.MenuRoleConverter;
import com.project.beauty_care.domain.menuRole.repository.MenuRoleRepository;
import com.project.beauty_care.domain.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuRoleService {
    private final MenuRoleRepository repository;
    private final MenuRoleConverter converter;

    public List<MenuRole> createMenuRoleWithMenuAndRole(Menu menu, List<Role> roles) {
        return roles.stream()
                .map(role -> converter.buildEntity(menu, role))
                .toList();
    }

    public void saveAllMenuRoles(List<MenuRole> menuRoles) {
        repository.saveAll(menuRoles);
    }
}
