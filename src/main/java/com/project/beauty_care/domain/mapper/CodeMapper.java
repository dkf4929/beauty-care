package com.project.beauty_care.domain.mapper;

import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CodeMapper {
    CodeMapper INSTANCE = Mappers.getMapper(CodeMapper.class);

    @Mapping(target = "id", source = "code.id")
    @Mapping(target = "children", source = "children")
    @Mapping(target = "name", source = "code.name")
    @Mapping(target = "description", source = "code.description")
    @Mapping(target = "sortNumber", source = "code.sortNumber")
    @Mapping(target = "isUse", source = "code.isUse")
    @Mapping(target = "createdDateTime", source = "code.createdDateTime")
    @Mapping(target = "updatedDateTime", source = "code.updatedDateTime")
    CodeResponse toResponse(Code code, List<CodeResponse> children);

    @Mapping(target = "id", source = "code.id")
    @Mapping(target = "name", source = "code.name")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "description", source = "code.description")
    @Mapping(target = "sortNumber", source = "code.sortNumber")
    @Mapping(target = "isUse", source = "code.isUse")
    @Mapping(target = "createdDateTime", source = "createdDateTime")
    @Mapping(target = "updatedDateTime", source = "updatedDateTime")
    CodeResponse toResponse(Code code);
}
