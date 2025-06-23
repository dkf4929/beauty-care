package com.project.beauty_care.domain.attachFile.service;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.attachFile.AttachFileValidator;
import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.code.service.CodeService;
import com.project.beauty_care.global.utils.FileUtils;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AttachFileServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private AttachFileService service;

    @MockitoBean
    private CodeService codeService;

    @MockitoBean
    private AttachFileValidator validator;

    @MockitoBean
    private FileUtils utils;

    @DisplayName("서버에 임시 파일을 업로드한다.")
    @Test
    void uploadTempFile() {
        // given
        final String EXTENSION = "png";

        MockMultipartFile file
                = new MockMultipartFile("file", "test.txt", "text/plain", "test file!".getBytes());

        final String STORED_FILE_NAME = UUID.randomUUID().toString();

        final String FILE_PATH = "/UPLOAD/TEMP/FILE/" + STORED_FILE_NAME;

        final List<MultipartFile> FILE_LIST = List.of(file);

        CodeResponse extension = buildCodeResponse(EXTENSION);

        TempFileDto tempFileDto = buildTempFile(FILE_PATH, file.getOriginalFilename(), STORED_FILE_NAME);

        // when
        doNothing().when(validator).validExtension(anySet(), any());

        when(codeService.findCodeByParentId(any())).thenReturn(List.of(extension));

        when(utils.uploadFileToServer(any(), any(), any()))
                .thenReturn(tempFileDto);

        List<TempFileDto> tempFileList = service.uploadTempFile(FILE_LIST);

        // then
        assertThat(tempFileList)
                .hasSize(1)
                .extracting("tempFileFullPath", "originalFileName", "storedFileName", "size")
                .containsExactly(
                        Tuple.tuple(FILE_PATH, file.getOriginalFilename(), STORED_FILE_NAME, 500L)
                );
    }

    @Test
    void deleteFile() {
    }

    @Test
    void uploadFile() {
    }

    @Test
    void saveFileAndConvertResponse() {
    }

    @Test
    void deleteTempFile() {
    }

    @Test
    void deleteTempFileSchedule() {
    }

    private CodeResponse buildCodeResponse(String extensionName) {
        return CodeResponse.builder()
                .name(extensionName)
                .build();
    }

    private TempFileDto buildTempFile(String filePath, String fileName, String storedFileName) {
        return TempFileDto.builder()
                .tempFileFullPath(filePath)
                .size(500L)
                .originalFileName(fileName)
                .storedFileName(storedFileName)
                .build();
    }
}