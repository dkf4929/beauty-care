package com.project.beauty_care.domain.attachFile;

import org.springframework.stereotype.Component;

@Component
public class AttachFileConverter {
    public AttachFile buildEntity(AttachContext attachContext,
                                  String fileName,
                                  String fileFullPath,
                                  String extension,
                                  long size) {
        return AttachFile.builder()
                .attachContext(attachContext)
                .fileName(fileName)
                .filePath(fileFullPath)
                .extension(extension)
                .fileSize(size)
                .build();
    }
}
