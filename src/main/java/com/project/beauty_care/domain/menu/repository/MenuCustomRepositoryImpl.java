package com.project.beauty_care.domain.menu.repository;

import com.project.beauty_care.domain.menuRole.QMenuRole;
import com.project.beauty_care.domain.role.QRole;
import com.project.beauty_care.domain.role.Role;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class MenuCustomRepositoryImpl implements MenuCustomRepository {
    private final JPAQueryFactory queryFactory;
    private final QMenuRole menuRole;
    private final QRole role;

    public MenuCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
        this.menuRole = QMenuRole.menuRole;
        this.role = QRole.role;
    }

    @Override
    public List<Role> findRolesByMenuId(Long menuId) {
        return queryFactory
                .select(menuRole.role)
                .from(menuRole)
                .join(menuRole.role, role)
                .where(menuRole.menu.id.eq(menuId))
                .fetch();
    }
}
