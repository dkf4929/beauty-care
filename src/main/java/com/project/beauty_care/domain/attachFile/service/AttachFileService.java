package com.project.beauty_care.domain.attachFile.service;

import com.project.beauty_care.domain.attachFile.AttachFile;
import com.project.beauty_care.domain.attachFile.AttachFileConverter;
import com.project.beauty_care.domain.attachFile.AttachFileRepository;
import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.domain.attachFile.dto.AttachFileCreateRequest;
import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachFileService {
    private final AttachFileRepository repository;
    private final AttachFileConverter converter;
    private final FileUtils fileUtils;

    @Value("${file.upload.real.dir}")
    private String realDir;

    @Value("${file.upload.temp.dir}")
    private String tempDir;

    @Transactional
    public List<TempFileDto> uploadTempFile(List<MultipartFile> files) {
        return files.stream()
                .map(file -> fileUtils.uploadFileToServer(file, tempDir))
                .toList();
    }

    @Transactional
    public void deleteFile(Long fileId) {
        AttachFile file = findById(fileId);

        // from db
        repository.delete(file);

        String fileFullPath = file.getFilePath() + "/" + file.getStoredFileName();

        fileUtils.deleteFileFromServer(fileFullPath);
    }

    @Transactional
    public List<AttachFile> uploadFile(AttachFileCreateRequest request) {
        return request.getTempFileList().stream()
                .map(tempFile -> {
                    Map<String, String> fileInfoMap = fileUtils.fileNameToMap(tempFile.getOriginalFileName(), realDir);
                    MappedEntity mappedEntity = request.getMappedEntity();
                    String mappedId = request.getMappedId();

                    String directory =
                            fileInfoMap.getOrDefault(FileUtils.FILE_PATH, "") + mappedEntity.name() + "/" + mappedId;

                    AttachFile file = converter.buildEntity(
                            mappedEntity,
                            mappedId,
                            fileInfoMap.getOrDefault(FileUtils.FILE_NAME, ""),
                            tempFile.getStoredFileName(),
                            directory,
                            fileInfoMap.getOrDefault(FileUtils.EXTENSION, ""),
                            tempFile.getSize());

                    // 파일과 DB 정합성을 위해 bulk insert 하지 않는다.
                    AttachFile savedEntity = repository.save(file);

                    fileUtils.moveTempFileToRealServer(
                            tempFile.getTempFileFullPath(),
                            request.getMappedEntity(),
                            request.getMappedId(),
                            realDir);

                    return savedEntity;
                })
                .toList();
    }

    // 임시 파일 삭제
    public void deleteTempFile(String tempFileFullPath) {
        fileUtils.deleteFileFromServer(tempFileFullPath);
    }

    // 스케줄러 -> 하루가 지난 임시 파일 삭제
    public void deleteTempFileSchedule(LocalDateTime now) {
        fileUtils.deleteTempFileAfterOneDay(now.minusDays(1), tempDir);
    }

    private AttachFile findById(Long fileId) {
        return repository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_FILE));
    }
}
