package com.project.beauty_care.global.utils;

import com.project.beauty_care.domain.attachFile.AttachFileConverter;
import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.FileUploadException;
import com.project.beauty_care.global.exception.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileUtils {
    private final AttachFileConverter converter;

    @Value("${file.upload.real.dir}")
    private String realDir;

    @Value("${file.upload.temp.dir}")
    private String tempDir;

    public static final String FILE_NAME = "fileName";
    public static final String EXTENSION = "extension";
    public static final String STORED_FILE_NAME = "storedFileName";
    public static final String FILE_PATH = "filePath";

    public void deleteFileFromServer(String filePath, String storedFileName) {
        Path path = Paths.get(filePath, storedFileName);

        try {
            boolean isDelete = Files.deleteIfExists(path);

            // 서버에서 파일이 삭제되지 않으면, 롤백처리
            if (!isDelete)
                throw new SystemException(Errors.CAN_NOT_DELETE_FILE);
        } catch (IOException e) {
            throw new SystemException(Errors.CAN_NOT_DELETE_FILE);
        }
    }

    // 파일을 실제 경로로 옮긴다.
    public void moveTempFileToRealServer(String tempFileFullPath, MappedEntity mappedEntity, String mappedId) {
        String fileName = getFileNameFromFullPath(tempFileFullPath);
        Path realFullPath = Paths.get(realDir, mappedEntity.name(), mappedId).resolve(fileName);
        Path tempFullPath = Paths.get(tempFileFullPath);

        try {
            boolean isExists = Files.exists(tempFullPath);

            if (!isExists) throw new FileUploadException(Errors.FILE_NOT_SAVED);

            Files.createDirectories(realFullPath.getParent());
            Files.move(tempFullPath, realFullPath);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileUploadException(Errors.FILE_NOT_SAVED);
        }
    }

    public TempFileDto uploadFileToServer(MultipartFile file) {
        Path filePath = Paths.get(tempDir);

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null)
            throw new FileUploadException(Errors.FILE_NOT_SAVED);

        String extension = getExtensionFromFileName(originalFilename);
        String storedFileName = createStoredFileName(extension);

        Path path = filePath.resolve(storedFileName);

        try {
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);
        } catch (java.io.IOException e) {
            log.error(e.getMessage());
            throw new FileUploadException(Errors.FILE_NOT_SAVED);
        }

        return converter.toDto(path.toString().replace("\\", "/"),
                originalFilename,
                file.getSize(),
                storedFileName);
    }

    public Map<String, String> fileNameToMap(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");

        String extension = fileName.substring(lastIndex + 1);

        String storedFileName = UUID.randomUUID() + "." + extension;

        return Map.of(
                FILE_NAME, fileName.substring(0, lastIndex),
                EXTENSION, extension,
                STORED_FILE_NAME, storedFileName,
                FILE_PATH, realDir
        );
    }

    public String getFileNameFromFullPath(String tempFileFullPath) {
        return tempFileFullPath.substring(tempFileFullPath.lastIndexOf("/") + 1);
    }

    public String getExtensionFromFileName(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");

        return fileName.substring(lastIndex + 1);
    }

    public LocalDateTime getFileUploadTime(String filePath) {
        Path path = Paths.get(filePath);

        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            return attrs.creationTime()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (IOException e) {
            throw new SystemException(Errors.INTERNAL_SERVER_ERROR);
        }
    }

    private String createStoredFileName(String extension) {
        return UUID.randomUUID() + "." + extension;
    }
}
