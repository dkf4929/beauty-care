package com.project.beauty_care.global.utils;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.code.service.CodeService;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.FileUploadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class FileUtilsTest extends TestSupportWithOutRedis {
    @Autowired
    private FileUtils fileUtils;

    @TempDir
    private Path tempDir;

    @TempDir
    private Path realDir;

    @MockitoBean
    private CodeService codeService;

    final byte[] MAX_FILE_SIZE = new byte[(int) (5 * 1024 * 1024)];

    final MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            MAX_FILE_SIZE);

    @DisplayName("파일을 서버에 업로드한다.")
    @TestFactory
    Collection<DynamicTest> uploadFileToServer() {
        return List.of(
                dynamicTest("정상 시나리오", () -> {
                    // given
                    when(codeService.findCodeByParentId(anyString()))
                            .thenReturn(List.of(buildCodeResponseOnlyWithCodeName()));

                    // when
                    TempFileDto tempFileDto = fileUtils.uploadFileToServer(
                            file, tempDir.toString(), fileUtils.getExtensionFromFileName(file.getOriginalFilename())
                    );

                    // then
                    Path expectedFullPath = tempDir.resolve(tempFileDto.getStoredFileName()).normalize().toAbsolutePath();

                    assertThat(tempFileDto).isNotNull()
                            .extracting(
                                    dto -> Paths.get(dto.getTempFileFullPath()).toAbsolutePath().normalize(),
                                    TempFileDto::getOriginalFileName,
                                    TempFileDto::getSize
                            )
                            .containsExactly(
                                    expectedFullPath,
                                    file.getOriginalFilename(),
                                    file.getSize()
                            );

                    // 업로드된 파일 삭제
                    String fileFullPath = tempDir.toString() + "/" + tempFileDto.getStoredFileName();

                    fileUtils.deleteFileFromServer(fileFullPath);

                    // 파일이 실제로 삭제되었는지 검증
                    boolean exists = isExists(tempDir.resolve(tempFileDto.getStoredFileName()));
                    assertThat(exists).isFalse();
                }),
                dynamicTest("파일 최대 사이즈 초과 -> 예외 발생", () -> {
                    // given
                    final byte[] exceedMaxFileSize = new byte[(int) (5 * 1024 * 1024) + 1];

                    MockMultipartFile invalidFile = new MockMultipartFile(
                            "file",
                            "large_file.txt",
                            "text/plain",
                            exceedMaxFileSize
                    );

                    // when & then
                    assertThatThrownBy(() -> fileUtils.uploadFileToServer(
                            invalidFile,
                            tempDir.toString(),
                            fileUtils.getExtensionFromFileName(invalidFile.getOriginalFilename())
                    ))
                            .isInstanceOf(FileUploadException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.EXCEED_MAX_FILE_SIZE.getMessage());
                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.EXCEED_MAX_FILE_SIZE.getErrorCode());
                            });
                })
        );
    }

    @DisplayName("파일명이 없는 파일 -> 예외 발생")
    @Test
    void uploadFileWithEmptyNameShouldInvokeError() {
        when(codeService.findCodeByParentId(anyString()))
                .thenReturn(List.of(buildCodeResponseOnlyWithCodeName()));

        // given
        MockMultipartFile fileWithBlankFileName = new MockMultipartFile(
                "test", "", "text/plain", "test content".getBytes()
        );

        // when, then
        assertThatThrownBy(() -> fileUtils.uploadFileToServer(fileWithBlankFileName, tempDir.toString(), fileUtils.getExtensionFromFileName(fileWithBlankFileName.getOriginalFilename())))
                .isInstanceOf(FileUploadException.class)
                .extracting("errors")
                .satisfies(errors -> {
                    assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.FILE_NOT_SAVED.getMessage());
                    assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.FILE_NOT_SAVED.getErrorCode());
                });
    }

    @DisplayName("임시 파일을 실제 파일 경로로 이동시킨다.")
    @Test
    void moveTempFileToRealServer() {
        // given
        when(codeService.findCodeByParentId(anyString()))
                .thenReturn(List.of(buildCodeResponseOnlyWithCodeName()));

        TempFileDto tempFileDto =
                fileUtils.uploadFileToServer(
                        file,
                        tempDir.toString(),
                        fileUtils.getExtensionFromFileName(file.getOriginalFilename())
                );

        Path expectedFullPath = tempDir.resolve(tempFileDto.getStoredFileName()).normalize().toAbsolutePath();

        assertThat(tempFileDto).isNotNull()
                .extracting(
                        dto -> Paths.get(dto.getTempFileFullPath()).toAbsolutePath().normalize(),
                        TempFileDto::getOriginalFileName,
                        TempFileDto::getSize
                )
                .containsExactly(
                        expectedFullPath,
                        file.getOriginalFilename(),
                        file.getSize()
                );

        final String mappedId = "1";

        // when : 파일 실제 경로로 이동
        fileUtils.moveTempFileToRealServer(
                tempFileDto.getTempFileFullPath(),
                MappedEntity.BOARD,
                mappedId,
                realDir.toString()
        );

        Path targetPath = Paths.get(realDir.toString(), MappedEntity.BOARD.name(), mappedId)
                .resolve(tempFileDto.getStoredFileName())
                .normalize();

        // then : 실제 경로에 존재하는지 확인 / 임시 저장 경로에서 사라졌는지 확인
        assertThat(isExists(targetPath)).isTrue();
        assertThat(isExists(Paths.get(tempFileDto.getTempFileFullPath()))).isFalse();
    }

    @DisplayName("하루가 지난 임시파일은 삭제한다.")
    @TestFactory
    Collection<DynamicTest> deleteTempFileAfterOneDay() {
        return List.of(
                dynamicTest("삭제 케이스(하루 지난 파일)", () -> {
                    // given
                    // 임시 파일을 하나 저장한다.
                    TempFileDto tempFileDto = uploadAndAssertTempFileExists();

                    Path filePath = Paths.get(tempFileDto.getTempFileFullPath());

                    // 파일 생성 시간 => 이틀전으로 설정.
                    FileTime fileTime
                            = FileTime.from(LocalDateTime.now().minusDays(2).atZone(ZoneId.systemDefault()).toInstant());

                    Files.setLastModifiedTime(filePath, fileTime);

                    // when
                    fileUtils.deleteTempFileAfterOneDay(LocalDateTime.now(), tempFileDto.getTempFileFullPath());

                    boolean exists = isExists(Paths.get(tempFileDto.getTempFileFullPath()));

                    // then
                    assertThat(exists).isFalse();
                }),
                dynamicTest("삭제 x (하루 지나지 않은 임시 파일)", () -> {
                    // given
                    // 임시 파일을 하나 저장한다.
                    TempFileDto tempFileDto = uploadAndAssertTempFileExists();

                    // when
                    fileUtils.deleteTempFileAfterOneDay(LocalDateTime.now().minusDays(1), tempFileDto.getTempFileFullPath());

                    boolean exists = isExists(Paths.get(tempFileDto.getTempFileFullPath()));

                    // then
                    assertThat(exists).isTrue();
                })
        );
    }

    private TempFileDto uploadAndAssertTempFileExists() {
        when(codeService.findCodeByParentId(anyString()))
                .thenReturn(List.of(buildCodeResponseOnlyWithCodeName()));

        TempFileDto tempFileDto =
                fileUtils.uploadFileToServer(
                        file,
                        tempDir.toString(),
                        fileUtils.getExtensionFromFileName(file.getOriginalFilename())
                );

        // then
        Path expectedFullPath = tempDir.resolve(tempFileDto.getStoredFileName()).normalize().toAbsolutePath();

        assertThat(tempFileDto).isNotNull()
                .extracting(
                        dto -> Paths.get(dto.getTempFileFullPath()).toAbsolutePath().normalize(),
                        TempFileDto::getOriginalFileName,
                        TempFileDto::getSize
                )
                .containsExactly(
                        expectedFullPath,
                        file.getOriginalFilename(),
                        file.getSize()
                );

        return tempFileDto;
    }

    private CodeResponse buildCodeResponseOnlyWithCodeName() {
        return CodeResponse.builder()
                .name("txt")
                .build();
    }

    private static boolean isExists(Path fullPath) {
        return Files.exists(fullPath);
    }
}