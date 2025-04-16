package com.project.beauty_care.domain.menu.service;

import com.project.beauty_care.domain.mapper.MenuMapper;
import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menu.dto.AdminMenuCreateRequest;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.menu.dto.AdminMenuUpdateRequest;
import com.project.beauty_care.domain.menu.repository.MenuRepository;
import com.project.beauty_care.domain.menuRole.MenuRole;
import com.project.beauty_care.domain.menuRole.service.MenuRoleService;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.service.RoleService;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.RedisCacheKey;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.RequestInvalidException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    private final MenuRepository repository;
    private final RoleService roleService;
    private final MenuRoleService menuRoleService;

    @CacheEvict(value = RedisCacheKey.MENU, allEntries = true, cacheManager = "redisCacheManager")
    public AdminMenuResponse createMenu(AdminMenuCreateRequest request) {
        Menu parent = null;

        // 메뉴 depth 최대 3
        validateMenuLevel(request);

        // 상위메뉴
        if (ObjectUtils.isNotEmpty(request.getParentMenuId())) {
            parent = findByParentId(request.getParentMenuId());
            // 상위 메뉴 "사용 중" 상태 아닌 경우 예외
            checkParentMenuIsUse(request, parent);
        }

        List<Role> roleList = roleService.findRoleByRoleNames(request.getRoleNames());
        Menu entity = buildEntity(request, parent);

        // save
        Menu savedEntity = repository.save(entity);

        // 연관관계 mapping
        List<MenuRole> menuRoles = menuRoleService.createMenuRoleWithMenuAndRole(entity, roleList);
        menuRoleService.saveAllMenuRoles(menuRoles);

        // convert response
        List<RoleResponse> roleResponseList = roleList.stream()
                .map(roleService::convertRoleToResponse)
                .toList();

        return MenuMapper.INSTANCE.toResponse(savedEntity, roleResponseList);
    }

    @Cacheable(value = RedisCacheKey.MENU, key = "'all'", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public AdminMenuResponse findAllMenu() {
        Optional<Menu> menuOptional = repository.findByParentIsNull();

        // 상위메뉴 empty => return
        if (menuOptional.isEmpty()) return null;

        Menu menu = menuOptional.get();

        // 계층형 구조 변환
        return convertHierarchyResponse(menu);
    }

    @CacheEvict(value = RedisCacheKey.MENU, allEntries = true, cacheManager = "redisCacheManager")
    public AdminMenuResponse updateMenu(AdminMenuUpdateRequest request, Long menuId) {
        Menu menu = findById(menuId);
        Menu parent = menu.getParent();

        // 최하위 메뉴에 leafMenu 추가 불가.
        validateParentMenu(request, parent);

        List<Role> roleList = roleService.findRoleByRoleNames(request.getRoleNames());
        List<RoleResponse> roleResponseList = roleList.stream().map(roleService::convertRoleToResponse).toList();

        List<MenuRole> menuRoleList = menuRoleService.createMenuRoleWithMenuAndRole(menu, roleList);

        // update menu
        menu.updateMenu(request, menuRoleList);

        return MenuMapper.INSTANCE.toResponse(menu, roleResponseList);
    }

    @Transactional(readOnly = true)
    public AdminMenuResponse findMenuById(Long id) {
        Menu menu = findById(id);

        List<RoleResponse> roleResponseList = menu.getMenuRole().stream()
                .map(MenuRole::getRole)
                .map(roleService::convertRoleToResponse)
                .toList();

        return MenuMapper.INSTANCE.toResponse(menu, roleResponseList);
    }

    private static void validateMenuLevel(AdminMenuCreateRequest request) {
        if (request.getMenuLevel() >= 2 && !request.getIsLeaf())
            throw new RequestInvalidException(Errors.CAN_NOT_SAVE_CHILDREN_MENU);
    }

    private void validateParentMenu(AdminMenuUpdateRequest request, Menu parent) {
        if (parent.getIsLeaf() && request.getIsLeaf())
            throw new RequestInvalidException(Errors.CAN_NOT_SAVE_CHILDREN_MENU);
    }

    private void checkParentMenuIsUse(AdminMenuCreateRequest request, Menu parent) {
        if (request.getIsUse() && !parent.getIsUse())
            throw new RequestInvalidException(Errors.PARENT_MENU_NOT_USE);
    }

    private AdminMenuResponse convertHierarchyResponse(Menu menu) {
        // 권한 목록
        List<RoleResponse> roleResponseList = menu.getMenuRole().stream()
                .map(MenuRole::getRole)
                .map(roleService::convertRoleToResponse)
                .toList();

        AdminMenuResponse response = MenuMapper.INSTANCE.toResponse(menu, roleResponseList);

        // 하위 메뉴 convert
        List<AdminMenuResponse> childrenList = menu.getChildren().stream()
                .map(this::convertHierarchyResponse)
                .toList();

        response.setChildren(new ArrayList<>(childrenList));

        return response;
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
