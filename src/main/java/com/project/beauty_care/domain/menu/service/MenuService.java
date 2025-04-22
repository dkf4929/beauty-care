package com.project.beauty_care.domain.menu.service;

import com.project.beauty_care.domain.mapper.MenuMapper;
import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menu.MenuConverter;
import com.project.beauty_care.domain.menu.MenuValidator;
import com.project.beauty_care.domain.menu.dto.AdminMenuCreateRequest;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.menu.dto.AdminMenuUpdateRequest;
import com.project.beauty_care.domain.menu.dto.UserMenuResponse;
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
import com.project.beauty_care.global.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final RedisUtils utils;

    @CacheEvict(value = RedisCacheKey.MENU, key = "'all'", cacheManager = "redisCacheManager")
    public AdminMenuResponse createMenu(AdminMenuCreateRequest request) {
        Menu parent = null;
        List<Role> roleList = List.of();

        // 메뉴 depth 최대 3
        validator.validateMenuLevelAndIsLeaf(request);

        // 상위메뉴 검증
        parent = validateParentMenu(request, parent);

        Menu entity = buildEntity(request, parent);

        // save
        Menu savedEntity = repository.save(entity);

        // 연관관계 mapping
        roleList = addRoleToMenu(request, roleList, entity);

        return convertResponse(roleList, savedEntity);
    }

    @Cacheable(
            value = RedisCacheKey.MENU,
            key = "#p0 != null ? #p0 : 'all'",
            cacheManager = "redisCacheManager"
    )
    @Transactional(readOnly = true)
    public AdminMenuResponse findAllMenu(String role) {
        Menu menu = repository.findByParentIsNull().orElse(null);

        // 상위메뉴 empty => return
        if (ObjectUtils.isEmpty(menu)) return AdminMenuResponse.builder().build();

        // 계층형 구조 변환
        return toHierarchyFromParent(menu, role);
    }

    public AdminMenuResponse updateMenu(AdminMenuUpdateRequest request, Long menuId) {
        Menu menu = findById(menuId);
        Menu parent = menu.getParent();

        // leaf menu validation
        validator.validateMenuLevelAndIsLeaf(request);

        // parent menu validation
        if (ObjectUtils.isNotEmpty(parent))
            validator.validateParentMenuIsUse(request, parent);

        List<Role> roleList = roleService.findRoleByRoleNames(request.getRoleNames());
        List<RoleResponse> roleResponseList = roleList.stream().map(roleConverter::toResponse).toList();

        List<MenuRole> menuRoleList = menuRoleService.createMenuRoleWithMenuAndRole(menu, roleList);

        // update menu
        menu.updateMenu(request, menuRoleList);

        // clear cache
        clearMenuCache(request);

        return converter.toResponse(menu, roleResponseList);
    }

    public UserMenuResponse findMenuByAuthority(String role) {
        // 최하위 메뉴
        List<Menu> leafMenuList = repository.findByIsLeafIsTrueAndIsUseIsTrue();

        // 최상위 메뉴
        Menu topMenu = repository.findByParentIsNull().orElse(null);

        UserMenuResponse response = UserMenuResponse.builder().build();

        Map<Long, UserMenuResponse> map = new HashMap<>();

        // 최하위 메뉴에서 부터 권한이 일치하는 메뉴를 찾아서 계층형 변환한다.
        leafMenuList.stream()
                .filter(menu -> menu.getMenuRole()
                        .stream()
                        .anyMatch(menuRole -> menuRole.getRole().getRoleName().equals(role))
                )
                .forEach(menu -> toHierarchyFromChildren(menu, response, map));

        // 최상위 메뉴
        return map.get(topMenu.getId());
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

    private void toHierarchyFromChildren(Menu menu,
                                         UserMenuResponse response,
                                         Map<Long, UserMenuResponse> map) {
        Menu parent = menu.getParent();

        // empty or isUse = false -> 다음 메뉴로
        if (ObjectUtils.isEmpty(parent) || !parent.getIsUse()) return;

        if (response.getChildren() == null) {
            response = MenuMapper.INSTANCE.toResponse(parent);
            UserMenuResponse childResponse = MenuMapper.INSTANCE.toResponse(menu);

            // 키(부모)가 존재하면 부모에 add
            if (map.containsKey(parent.getId())) map.get(parent.getId()).getChildren().add(childResponse);
            else {
                // 없으면 새로 생성하고 put
                response.setChildren(new ArrayList<>(List.of(childResponse)));
                map.put(parent.getId(), response);
                toHierarchyFromChildren(parent, response, map);
            }
        } else {
            // 계층형 구조 만든다.
            UserMenuResponse parentResponse;

            if (map.containsKey(parent.getId())) {
                parentResponse = map.get(parent.getId());
                parentResponse.getChildren().add(response);
            } else {
                parentResponse = MenuMapper.INSTANCE.toResponse(parent);
                parentResponse.setChildren(new ArrayList<>(List.of(response)));
                map.put(parent.getId(), parentResponse);
            }

            response = parentResponse;
            toHierarchyFromChildren(parent, response, map);
        }
    }

    private static boolean menuHasRole(Role role, Menu children) {
        return children.getMenuRole().stream().anyMatch(menuRole -> menuRole.getRole().equals(role));
    }

    private AdminMenuResponse toHierarchyFromParent(Menu menu, String role) {
        // 권한 목록
        List<RoleResponse> roleResponseList = roleConverter.toResponseWithMenu(menu);

        AdminMenuResponse response = converter.toResponse(menu, roleResponseList);

        // 하위 메뉴 convert
        List<AdminMenuResponse> childrenList = menu.getChildren().stream()
                .filter(child -> {
                    // 최하위 메뉴 아님 -> pass
                    if (!child.getIsLeaf()) return true;

                    // 권한이 존재하는 메뉴만 필터링
                    return child.getMenuRole().stream()
                            .map(menuRole -> menuRole.getRole().getRoleName())
                            .anyMatch(roleId -> roleId.equals(role));
                })
                .map(child -> toHierarchyFromParent(child, role))
                .toList();

        response.setChildren(new ArrayList<>(childrenList));

        return response;
    }

    private Menu validateParentMenu(AdminMenuCreateRequest request, Menu parent) {
        if (ObjectUtils.isNotEmpty(request.getParentMenuId())) {
            parent = findByParentId(request.getParentMenuId());
            // 상위 메뉴 "사용 중" 상태 아닌 경우 예외
            validator.validateParentMenuIsUse(request, parent);
            // parent menu leaf => ex
            validator.validateParentMenuIsLeafFalse(parent.getIsLeaf());
        }
        return parent;
    }

    private List<Role> addRoleToMenu(AdminMenuCreateRequest request, List<Role> roleList, Menu entity) {
        List<MenuRole> menuRoles;
        if (!request.getRoleNames().isEmpty()) {
            roleList = roleService.findRoleByRoleNames(request.getRoleNames());
            menuRoles = menuRoleService.createMenuRoleWithMenuAndRole(entity, roleList);

            menuRoleService.saveAllMenuRoles(menuRoles);
        }
        return roleList;
    }

    private AdminMenuResponse convertResponse(List<Role> roleList, Menu savedEntity) {
        List<RoleResponse> roleResponseList = roleList.stream()
                .map(roleConverter::toResponse)
                .toList();

        return converter.toResponse(savedEntity, roleResponseList);
    }

    private void clearMenuCache(AdminMenuUpdateRequest request) {
        utils.clearCacheByKey(RedisCacheKey.MENU, request.getRoleNames());
        utils.clearCacheByKey(RedisCacheKey.MENU_ROLE, request.getRoleNames());
    }

}
