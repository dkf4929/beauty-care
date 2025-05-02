package com.project.beauty_care.global.utils;

import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.FileUploadException;
import com.project.beauty_care.global.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class FileUtils {

    public void deleteFileFromServer(Path filePath) {
        try {
            boolean isDelete = Files.deleteIfExists(filePath);

            // 서버에서 파일이 삭제되지 않으면, 롤백처리
            if (!isDelete)
                throw new SystemException(Errors.CAN_NOT_DELETE_FILE);
        } catch (IOException e) {
            throw new SystemException(Errors.CAN_NOT_DELETE_FILE);
        }
    }

    public Map<String, String> uploadFileToServer(String directory,
                                                  String originalFileName,
                                                  MultipartFile file) {
        Map<String, String> map = extractExtension(originalFileName);
        Path path = Paths.get(directory).resolve(map.getOrDefault("storedFileName", ""));

        try {
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);
        } catch (java.io.IOException e) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                throw new SystemException(Errors.CAN_NOT_DELETE_FILE);
            }
            log.error(e.getMessage());
            throw new FileUploadException(Errors.FILE_NOT_SAVED);
        }

        return map;
    }

    private Map<String, String> extractExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");

        if (lastIndex == -1) return Map.of("fileName", fileName, "extension", "");

        String extension = fileName.substring(lastIndex + 1);

        String storedFileName = UUID.randomUUID() + "." + extension;

        return Map.of(
                "fileName", fileName.substring(0, lastIndex),
                "extension", extension,
                "storedFileName", storedFileName
        );
    }
}
