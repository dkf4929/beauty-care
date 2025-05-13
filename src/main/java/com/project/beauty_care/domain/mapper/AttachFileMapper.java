package com.project.beauty_care.domain.mapper;

import com.project.beauty_care.domain.attachFile.AttachFile;
import com.project.beauty_care.domain.attachFile.dto.AttachFileResponse;
import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AttachFileMapper {
    AttachFileMapper INSTANCE = Mappers.getMapper(AttachFileMapper.class);

    @Mapping(target = "tempFileFullPath", source = "tempFileFullPath")
    @Mapping(target = "originalFileName", source = "originalFileName")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "storedFileName", source = "storedFileName")
    TempFileDto toDto(String tempFileFullPath, String originalFileName, Long size, String storedFileName);

    @Mapping(target = "fileId", source = "file.id")
    @Mapping(target = "fileFullPath", source = "fileFullPath")
    @Mapping(target = "originalFileName", source = "file.fileName")
    @Mapping(target = "fileSize", source = "file.fileSize")
    @Mapping(target = "createdDateTime", source = "file.createdDateTime")
    @Mapping(target = "updatedDateTime", source = "file.updatedDateTime")
    AttachFileResponse toResponse(AttachFile file, String fileFullPath);
}
