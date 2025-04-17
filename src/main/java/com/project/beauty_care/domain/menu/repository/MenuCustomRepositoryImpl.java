package com.project.beauty_care.domain.menu.repository;

import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menu.QMenu;
import com.project.beauty_care.domain.menuRole.QMenuRole;
import com.project.beauty_care.domain.role.QRole;
import com.project.beauty_care.domain.role.Role;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class MenuCustomRepositoryImpl implements MenuCustomRepository {
    private final JPAQueryFactory queryFactory;
    private final QMenu menu;
    private final QMenuRole menuRole;
    private final QRole role;

    public MenuCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
        this.menuRole = QMenuRole.menuRole;
        this.role = QRole.role;
        this.menu = QMenu.menu;
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

    @Override
    public Menu findByRoleAndParentIsNull(String roleName) {
        return queryFactory.selectFrom(menu)
                .join(menu.menuRole, menuRole).fetchJoin() // menu → menuRoles
                .join(menuRole.role, role).fetchJoin()
                .where(eqRole(roleName).and(menu.parent.isNull()))
                .fetchOne();
    }


    private BooleanExpression eqRole(String roleName) {
        if (roleName.isEmpty()) return null;

        return role.roleName.eq(roleName);
    }
}
