package com.project.beauty_care.domain.mapper;

import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.menu.dto.UserMenuResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MenuMapper {
    MenuMapper INSTANCE = Mappers.getMapper(MenuMapper.class);

    @Mapping(target = "menuId", source = "menu.id")
    @Mapping(target = "menuName", source = "menu.menuName")
    @Mapping(target = "menuPath", source = "menu.menuPath")
    @Mapping(target = "sortNumber", source = "menu.sortNumber")
    @Mapping(target = "isLeaf", source = "menu.isLeaf")
    @Mapping(target = "isUse", source = "menu.isUse")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "createdDateTime", source = "menu.createdDateTime")
    @Mapping(target = "updatedDateTime", source = "menu.updatedDateTime")
    @Mapping(target = "createdBy", source = "menu.createdBy")
    @Mapping(target = "updatedBy", source = "menu.updatedBy")
    AdminMenuResponse toResponse(Menu menu, List<RoleResponse> roles);

    @Mapping(target = "menuName", source = "menu.menuName")
    @Mapping(target = "menuPath", source = "menu.menuPath")
    @Mapping(target = "sortNumber", source = "menu.sortNumber")
    @Mapping(target = "isLeaf", source = "menu.isLeaf")
    @Mapping(target = "children", ignore = true)
    UserMenuResponse toResponse(Menu menu);
}
