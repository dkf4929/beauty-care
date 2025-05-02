package com.project.beauty_care.domain.attachFile;

import org.springframework.stereotype.Component;

@Component
public class AttachFileConverter {
    public AttachFile buildEntity(MappedEntity mappedEntity,
                                  String mappedEntityId,
                                  String fileName,
                                  String storedFileName,
                                  String fileFullPath,
                                  String extension,
                                  long size) {
        return AttachFile.builder()
                .mappedEntity(mappedEntity)
                .mappedEntityId(mappedEntityId)
                .fileName(fileName)
                .storedFileName(storedFileName)
                .filePath(fileFullPath)
                .extension(extension)
                .fileSize(size)
                .build();
    }
}
