package com.project.beauty_care.domain.mapper;

import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "loginId", source = "loginId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "lastLoginDateTime", source = "lastLoginDateTime")
    @Mapping(target = "isUse", source = "isUse")
    @Mapping(target = "createdDateTime", source = "createdDateTime")
    @Mapping(target = "updatedDateTime", source = "updatedDateTime")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    MemberResponse toDto(Member member);
}
