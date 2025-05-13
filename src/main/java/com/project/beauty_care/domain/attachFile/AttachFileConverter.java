package com.project.beauty_care.domain.attachFile;

import com.project.beauty_care.domain.attachFile.dto.AttachFileCreateRequest;
import com.project.beauty_care.domain.attachFile.dto.AttachFileResponse;
import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import com.project.beauty_care.domain.mapper.AttachFileMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttachFileConverter {
    public AttachFile buildEntity(MappedEntity mappedEntity,
                                  String mappedId,
                                  String fileName,
                                  String storedFileName,
                                  String fileFullPath,
                                  String extension,
                                  long size) {
        return AttachFile.builder()
                .mappedEntity(mappedEntity)
                .mappedId(mappedId)
                .fileName(fileName)
                .storedFileName(storedFileName)
                .filePath(fileFullPath)
                .extension(extension)
                .fileSize(size)
                .build();
    }

    public AttachFileCreateRequest buildRequest(MappedEntity mappedEntity, String mappedId, List<TempFileDto> tempFileList) {
        return AttachFileCreateRequest.builder()
                .mappedEntity(mappedEntity)
                .mappedId(mappedId)
                .tempFileList(tempFileList)
                .build();
    }

    public TempFileDto toDto(String tempFileFullPath, String originalFileName, Long size, String storedFileName) {
        return AttachFileMapper.INSTANCE.toDto(tempFileFullPath, originalFileName, size, storedFileName);
    }

    public AttachFileResponse toResponse(AttachFile file, String fileFullPath) {
        return AttachFileMapper.INSTANCE.toResponse(file, fileFullPath);
    }
}
