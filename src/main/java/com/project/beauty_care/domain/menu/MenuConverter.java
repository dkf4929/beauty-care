package com.project.beauty_care.domain.menu;

import com.project.beauty_care.domain.mapper.MenuMapper;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.role.RoleConverter;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuConverter {
    private final RoleConverter roleConverter;

    public AdminMenuResponse toHierarchy(Menu menu) {
        // 권한 목록
        List<RoleResponse> roleResponseList = roleConverter.toResponseWithMenu(menu);

        AdminMenuResponse response = toResponse(menu, roleResponseList);

        // 하위 메뉴 convert
        List<AdminMenuResponse> childrenList = menu.getChildren().stream()
                .map(this::toHierarchy)
                .toList();

        response.setChildren(new ArrayList<>(childrenList));

        return response;
    }

    public AdminMenuResponse toHierarchy(Menu menu, String role) {
        // 권한 목록
        List<RoleResponse> roleResponseList = roleConverter.toResponseWithMenu(menu);

        AdminMenuResponse response = toResponse(menu, roleResponseList);

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
                .map(child -> toHierarchy(child, role))
                .toList();

        response.setChildren(new ArrayList<>(childrenList));

        return response;
    }

    public AdminMenuResponse toResponse(Menu menu, List<RoleResponse> roleResponseList) {
        return MenuMapper.INSTANCE.toResponse(menu, roleResponseList);
    }

}
