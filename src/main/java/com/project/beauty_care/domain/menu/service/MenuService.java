package com.project.beauty_care.domain.menu.service;

import com.project.beauty_care.domain.mapper.MenuMapper;
import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menu.dto.AdminMenuRequest;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.menu.repository.MenuRepository;
import com.project.beauty_care.domain.menuRole.MenuRole;
import com.project.beauty_care.domain.menuRole.repository.MenuRoleRepository;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.RedisCacheKey;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
    private final RoleRepository roleRepository;
    private final MenuRoleRepository menuRoleRepository;

    public AdminMenuResponse createMenu(AdminMenuRequest request) {
        Menu parent = null;

        // 상위메뉴
        if (request.getParentMenuId() != null) {
            parent = findByParentIdAndIsUseTrue(request.getParentMenuId());
        }

        List<Role> roleList = findRoleByRoleNames(request.getRoleNames());

        Menu entity = buildEntity(request, parent);

        // save
        Menu savedEntity = repository.save(entity);

        // 연관관계 mapping
        List<MenuRole> menuRoleList = roleList.stream()
                .map(role -> buildMenuRole(entity, role))
                .toList();

        menuRoleRepository.saveAll(menuRoleList);

        // convert response
        List<RoleResponse> roleResponseList = roleList.stream()
                .map(role -> RoleMapper.INSTANCE.toResponse(role, RoleResponse.patternMapToList(role.getUrlPatterns())))
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

    private AdminMenuResponse convertHierarchyResponse(Menu menu) {
        // 권한 목록
        List<RoleResponse> roleResponseList = menu.getMenuRole().stream()
                .map(menuRole -> {
                    Role role = menuRole.getRole();

                    return RoleMapper.INSTANCE.toResponse(role, RoleResponse.patternMapToList(role.getUrlPatterns()));
                })
                .toList();

        AdminMenuResponse response = MenuMapper.INSTANCE.toResponse(menu, roleResponseList);

        // 하위 메뉴 convert
        List<AdminMenuResponse> childrenList = menu.getChildren().stream()
                .map(this::convertHierarchyResponse)
                .toList();

        response.setChildren(new ArrayList<>(childrenList));

        return response;
    }

    private static MenuRole buildMenuRole(Menu entity, Role role) {
        return MenuRole.builder()
                .menu(entity)
                .role(role)
                .build();
    }

    private static Menu buildEntity(AdminMenuRequest request, Menu parent) {
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

    private List<Role> findRoleByRoleNames(List<String> roleNames) {
        return roleRepository.findAllById(roleNames);
    }

    private Menu findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_MENU));
    }

    private Menu findByParentIdAndIsUseTrue(Long parentId) {
        return repository.findByIdAndIsUseIsTrue(parentId)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_USE_PARENT_MENU));
    }

}
