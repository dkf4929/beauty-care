package com.project.beauty_care.domain.mapper;

import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.dto.MemberRoleResponse;
import com.project.beauty_care.domain.member.dto.MemberSummaryResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "loginId", source = "loginId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "role", source = "member.role.roleName")
    @Mapping(target = "lastLoginDateTime", source = "lastLoginDateTime")
    @Mapping(target = "isUse", source = "isUse")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "createdDateTime", source = "createdDateTime")
    @Mapping(target = "updatedDateTime", source = "updatedDateTime")
    MemberResponse toResponse(Member member);

    @Mapping(target = "id", source = "member.id")
    @Mapping(target = "loginId", source = "member.loginId")
    @Mapping(target = "name", source = "member.name")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "lastLoginDateTime", source = "member.lastLoginDateTime")
    @Mapping(target = "isUse", source = "member.isUse")
    @Mapping(target = "createdBy", source = "member.createdBy")
    @Mapping(target = "updatedBy", source = "member.updatedBy")
    @Mapping(target = "createdDateTime", source = "member.createdDateTime")
    @Mapping(target = "updatedDateTime", source = "member.updatedDateTime")
    MemberRoleResponse toResponse(Member member, RoleResponse role);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "loginId", source = "loginId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastLoginDateTime", source = "lastLoginDateTime")
    @Mapping(target = "isUse", source = "isUse")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "createdDateTime", source = "createdDateTime")
    @Mapping(target = "updatedDateTime", source = "updatedDateTime")
    MemberSummaryResponse toSummaryResponse(Member member);
}
