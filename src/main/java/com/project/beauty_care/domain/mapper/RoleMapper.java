package com.project.beauty_care.domain.mapper;

import com.project.beauty_care.domain.member.dto.MemberSummaryResponse;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.dto.RoleMemberResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(target = "roleName", source = "role.roleName")
    @Mapping(target = "urlPatterns", source = "urlPatterns")
    @Mapping(target = "isUse", source = "role.isUse")
    @Mapping(target = "createdBy", source = "role.createdBy")
    @Mapping(target = "updatedBy", source = "role.updatedBy")
    @Mapping(target = "createdDateTime", source = "role.createdDateTime")
    @Mapping(target = "updatedDateTime", source = "role.updatedDateTime")
    RoleResponse toDto(Role role, List<String> urlPatterns);

    @Mapping(target = "roleName", source = "role.roleName")
    @Mapping(target = "urlPatterns", source = "urlPatterns")
    @Mapping(target = "members", source = "members")
    @Mapping(target = "createdBy", source = "role.createdBy")
    @Mapping(target = "updatedBy", source = "role.updatedBy")
    @Mapping(target = "createdDateTime", source = "role.createdDateTime")
    @Mapping(target = "updatedDateTime", source = "role.updatedDateTime")
    RoleMemberResponse toDto(Role role, List<String> urlPatterns, List<MemberSummaryResponse> members);
}
