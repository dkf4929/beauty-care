package com.project.beauty_care.domain.attachFile.service;

import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.domain.attachFile.AttachFile;
import com.project.beauty_care.domain.attachFile.AttachFileConverter;
import com.project.beauty_care.domain.attachFile.AttachFileRepository;
import com.project.beauty_care.domain.attachFile.dto.AttachFileCreateRequest;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachFileService {
    private final AttachFileRepository repository;
    private final AttachFileConverter converter;

    @Value("${file.upload.dir}")
    private String uploadDir;

    public void createFile(List<MultipartFile> files, MappedEntity mappedEntity, String id) {
        List<AttachFile> attachFileList = uploadFile(files, mappedEntity, id);

        if (!attachFileList.isEmpty()) repository.saveAll(attachFileList);
    }

    // TODO : 파일명 중복 문제, 파일 정합성 스케줄러 처리?, 파일에 대한 업무 단위 문제..
    private List<AttachFile> uploadFile(List<MultipartFile> files, MappedEntity mappedEntity, String id) {
        return files.stream()
                .map(file -> {
                    String fileName = file.getOriginalFilename();
                    Path directory = Paths.get(uploadDir, mappedEntity.toString());
                    Path fileFullPath = directory.resolve(fileName);

                    long size = file.getSize();

                    Path path = Paths.get(uploadDir + "/" + mappedEntity).resolve(fileName);
                    String extension = extractExtension(fileName);

                    try {
                        Files.createDirectories(path.getParent());
                        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    } catch (java.io.IOException e) {
                        log.error(e.getMessage());
                        throw new FileUploadException(Errors.FILE_NOT_SAVED);
                    }

                    return converter.buildEntity(mappedEntity,
                            id,
                            fileName,
                            fileFullPath.toString(),
                            extension,
                            size);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private String extractExtension(String fileName) {
        int index = fileName.lastIndexOf('.');

        if (index > 0 && index < fileName.length() - 1)
            return fileName.substring(index + 1);

        return "";
    }
}
