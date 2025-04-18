package com.project.beauty_care.domain.menu;

import com.project.beauty_care.domain.mapper.MenuMapper;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.menu.dto.UserMenuResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuConverter {
    public AdminMenuResponse toResponse(Menu menu, List<RoleResponse> roleResponseList) {
        return MenuMapper.INSTANCE.toResponse(menu, roleResponseList);
    }

    public UserMenuResponse toResponse(Menu menu) {
        return MenuMapper.INSTANCE.toResponse(menu);
    }
}
