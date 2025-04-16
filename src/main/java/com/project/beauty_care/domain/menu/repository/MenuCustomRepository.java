package com.project.beauty_care.domain.menu.repository;

import com.project.beauty_care.domain.role.Role;

import java.util.List;

public interface MenuCustomRepository {
    public List<Role> findRolesByMenuId(Long menuId);
}
