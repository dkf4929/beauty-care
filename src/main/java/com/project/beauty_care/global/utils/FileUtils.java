package com.project.beauty_care.global.utils;

import com.project.beauty_care.domain.attachFile.AttachFileConverter;
import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.FileUploadException;
import com.project.beauty_care.global.exception.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileUtils {
    private final AttachFileConverter converter;
    public static final String FILE_NAME = "fileName";
    public static final String EXTENSION = "extension";
    public static final String STORED_FILE_NAME = "storedFileName";
    public static final String FILE_PATH = "filePath";

    public void deleteFileFromServer(String fileFullPath) {
        Path path = Paths.get(fileFullPath);

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
    public void moveTempFileToRealServer(String tempFileFullPath,
                                         MappedEntity mappedEntity,
                                         String mappedId,
                                         String realDir) {
        // 파일명
        String fileName = getFileNameFromFullPath(tempFileFullPath);

        // 파일 경로
        Path realFullPath = Paths.get(realDir, mappedEntity.name(), mappedId).resolve(fileName);
        Path tempFullPath = Paths.get(tempFileFullPath);

        try {
            // 파일 존재 여부
            boolean isExists = Files.exists(tempFullPath);

            if (!isExists) throw new FileUploadException(Errors.FILE_NOT_SAVED);

            // 폴더 x -> 생성
            Files.createDirectories(realFullPath.getParent());

            // 임시 경로 -> 실제 파일 경로 이동
            Files.move(tempFullPath, realFullPath);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileUploadException(Errors.FILE_NOT_SAVED);
        }
    }

    public TempFileDto uploadFileToServer(MultipartFile file, String tempDir, String extension) {
        Path filePath = Paths.get(tempDir);

        String originalFilename = file.getOriginalFilename();

        // 파일명 x -> 예외
        if (Objects.requireNonNull(originalFilename).isEmpty())
            throw new FileUploadException(Errors.FILE_NOT_SAVED);

        // 서버 파일명
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

    public String getExtensionFromFileName(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");

        return fileName.substring(lastIndex + 1);
    }

    public Map<String, String> fileNameToMap(String fileName, String realDir) {
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

    // 하루가 지난 임시파일들을 삭제한다.
    public void deleteTempFileAfterOneDay(LocalDateTime time, String tempDir) {
        try {
            Files.walk(Paths.get(tempDir))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        // 파일 업로드 시간
                        LocalDateTime fileUploadTime = getFileUploadTime(path);

                        if (fileUploadTime.isBefore(time)) {
                            try {
                                // 파일 삭제 (존재하면)
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                throw new SystemException(Errors.CAN_NOT_DELETE_FILE);
                            }
                        }
                    });

        } catch (IOException e) {
            throw new SystemException(Errors.CAN_NOT_DELETE_FILE);
        }
    }

    public LocalDateTime getFileUploadTime(Path path) {
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

    private String getFileNameFromFullPath(String tempFileFullPath) {
        return tempFileFullPath.substring(tempFileFullPath.lastIndexOf("/") + 1);
    }
}
