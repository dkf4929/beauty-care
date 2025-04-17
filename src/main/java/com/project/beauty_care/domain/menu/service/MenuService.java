package com.project.beauty_care.domain.menu.service;

import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menu.MenuConverter;
import com.project.beauty_care.domain.menu.MenuValidator;
import com.project.beauty_care.domain.menu.dto.AdminMenuCreateRequest;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.menu.dto.AdminMenuUpdateRequest;
import com.project.beauty_care.domain.menu.repository.MenuRepository;
import com.project.beauty_care.domain.menuRole.MenuRole;
import com.project.beauty_care.domain.menuRole.service.MenuRoleService;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.RoleConverter;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.service.RoleService;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.RedisCacheKey;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    private final MenuRepository repository;
    private final RoleService roleService;
    private final MenuRoleService menuRoleService;
    private final MenuConverter converter;
    private final RoleConverter roleConverter;
    private final MenuValidator validator;

    @CacheEvict(value = RedisCacheKey.MENU, allEntries = true, cacheManager = "redisCacheManager")
    public AdminMenuResponse createMenu(AdminMenuCreateRequest request) {
        Menu parent = null;
        List<MenuRole> menuRoles;
        List<Role> roleList = List.of();

        // 메뉴 depth 최대 3
        validator.validateMenuLevelAndIsLeaf(request);

        // 상위메뉴
        if (ObjectUtils.isNotEmpty(request.getParentMenuId())) {
            parent = findByParentId(request.getParentMenuId());
            // 상위 메뉴 "사용 중" 상태 아닌 경우 예외
            validator.validateParentMenuIsUse(request, parent);
        }

        Menu entity = buildEntity(request, parent);

        // save
        Menu savedEntity = repository.save(entity);

        // 연관관계 mapping
        if (!request.getRoleNames().isEmpty()) {
            roleList = roleService.findRoleByRoleNames(request.getRoleNames());
            menuRoles = menuRoleService.createMenuRoleWithMenuAndRole(entity, roleList);

            menuRoleService.saveAllMenuRoles(menuRoles);
        }

        // convert response
        List<RoleResponse> roleResponseList = roleList.stream()
                .map(roleService::convertRoleToResponse)
                .toList();

        return converter.toResponse(savedEntity, roleResponseList);
    }

    @Cacheable(value = RedisCacheKey.MENU, key = "#p0", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public AdminMenuResponse findAllMenu(String role) {
        Menu menu = repository.findByParentIsNull()
                .orElse(null);

        // 상위메뉴 empty => return
        if (ObjectUtils.isEmpty(menu)) return AdminMenuResponse.builder().build();

        // 계층형 구조 변환
        return converter.toHierarchy(menu, role);
    }

    @CacheEvict(value = RedisCacheKey.MENU, allEntries = true, cacheManager = "redisCacheManager")
    public AdminMenuResponse updateMenu(AdminMenuUpdateRequest request, Long menuId) {
        Menu menu = findById(menuId);
        Menu parent = menu.getParent();

        // 최하위 메뉴에 leafMenu 추가 불가.
        validator.validateParentMenu(request, parent);

        List<Role> roleList = roleService.findRoleByRoleNames(request.getRoleNames());
        List<RoleResponse> roleResponseList = roleList.stream().map(roleService::convertRoleToResponse).toList();

        List<MenuRole> menuRoleList = menuRoleService.createMenuRoleWithMenuAndRole(menu, roleList);

        // update menu
        menu.updateMenu(request, menuRoleList);

        return converter.toResponse(menu, roleResponseList);
    }

    @Transactional(readOnly = true)
    public AdminMenuResponse findMenuById(Long id) {
        Menu menu = findById(id);

        List<RoleResponse> roleResponseList = roleConverter.toResponseWithMenu(menu);

        return converter.toResponse(menu, roleResponseList);
    }

    private Menu buildEntity(AdminMenuCreateRequest request, Menu parent) {
        return Menu.builder()
                .menuName(request.getMenuName())
                .menuPath(request.getMenuPath())
                .description(request.getDescription())
                .isUse(request.getIsUse())
                .isLeaf(request.getIsLeaf())
                .sortNumber(request.getSortNumber())
                .parent(parent)
                .build();
    }

    private Menu findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_MENU));
    }

    private Menu findByParentId(Long parentId) {
        return repository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_PARENT_MENU));
    }
}
