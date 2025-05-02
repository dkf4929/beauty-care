package com.project.beauty_care.domain.attachFile.service;

import com.project.beauty_care.domain.attachFile.AttachFile;
import com.project.beauty_care.domain.attachFile.AttachFileConverter;
import com.project.beauty_care.domain.attachFile.AttachFileRepository;
import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.FileUploadException;
import com.project.beauty_care.global.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachFileService {
    private final AttachFileRepository repository;
    private final AttachFileConverter converter;
    private final FileUtils fileUtils;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Transactional
    public void createFile(List<MultipartFile> files, MappedEntity mappedEntity, String id) {
        List<AttachFile> attachFileList = uploadFile(files, mappedEntity, id);

        if (!attachFileList.isEmpty()) repository.saveAll(attachFileList);
    }

    @Transactional
    public void deleteFile(Long fileId) {
        AttachFile file = findById(fileId);

        // from db
        repository.delete(file);

        fileUtils.deleteFileFromServer(
                Paths.get(file.getFilePath(), file.getStoredFileName()));
    }

    private List<AttachFile> uploadFile(List<MultipartFile> files, MappedEntity mappedEntity, String id) {
        return files.stream()
                .map(file -> {
                    String originalFilename = file.getOriginalFilename();

                    if (originalFilename == null)
                        throw new FileUploadException(Errors.FILE_NOT_SAVED);

                    Path directory = Paths.get(uploadDir, mappedEntity.name());
                    final long size = file.getSize();

                    Map<String, String> map =
                            fileUtils.uploadFileToServer(directory.toString(), originalFilename, file);

                    return converter.buildEntity(mappedEntity,
                            id,
                            map.getOrDefault("fileName", ""),
                            map.get("storedFileName"),
                            directory.toString(),
                            map.getOrDefault("extension", ""),
                            size);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private AttachFile findById(Long fileId) {
        return repository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_FILE));
    }
}
