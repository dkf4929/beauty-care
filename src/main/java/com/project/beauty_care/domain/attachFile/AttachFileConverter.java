package com.project.beauty_care.domain.attachFile;

import org.springframework.stereotype.Component;

@Component
public class AttachFileConverter {
    public AttachFile buildEntity(MappedEntity mappedEntity,
                                  String mappedEntityId,
                                  String fileName,
                                  String fileFullPath,
                                  String extension,
                                  long size) {
        return AttachFile.builder()
                .mappedEntity(mappedEntity)
                .mappedEntityId(mappedEntityId)
                .fileName(fileName)
                .filePath(fileFullPath)
                .extension(extension)
                .fileSize(size)
                .build();
    }
}
