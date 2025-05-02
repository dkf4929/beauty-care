package com.project.beauty_care.domain.attachFile.controller;

import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.domain.attachFile.service.AttachFileService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "FILE REST API FOR PUBLIC", description = "파일 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/file")
public class AttachFileController {
    private final AttachFileService service;

    @PostMapping(value = "/mapped/{mappedEntity}/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse uploadFile(@RequestPart("file")
                                          @Parameter(description = "업로드할 파일", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                          List<MultipartFile> files,
                                      @PathVariable("mappedEntity") MappedEntity mappedEntity,
                                      @PathVariable("id") String id) {
        service.createFile(files, mappedEntity, id);
        return SuccessResponse.success(SuccessCodes.FILE_UPLOAD_SUCCESS);
    }

    @DeleteMapping("{fileId}")
    public SuccessResponse deleteFile(@RequestParam("fileId") Long fileId) {
        service.deleteFile(fileId);
        return SuccessResponse.success(SuccessCodes.DELETE_SUCCESS);
    }
}
