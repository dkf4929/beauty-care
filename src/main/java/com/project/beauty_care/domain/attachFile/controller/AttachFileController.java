package com.project.beauty_care.domain.attachFile.controller;

import com.project.beauty_care.domain.attachFile.dto.AttachFileCreateRequest;
import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import com.project.beauty_care.domain.attachFile.service.AttachFileService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "FILE REST API FOR USER", description = "파일 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/file")
public class AttachFileController {
    private final AttachFileService service;

    @Operation(summary = "upload temp file",
            description = "파일을 임시 저장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일이 업로드 되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TempFileDto.class))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"파일 업로드 중 오류가 발생했습니다.\" }"))),
    })
    @PostMapping(value = "/temp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<List<TempFileDto>> uploadTempFile(@RequestPart("file")
                                          @Parameter(description = "업로드할 파일",
                                                  content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                              List<MultipartFile> files) {
        return SuccessResponse.success(SuccessCodes.FILE_UPLOAD_SUCCESS, service.uploadTempFile(files));
    }

    @Operation(summary = "upload file",
            description = "파일을 저장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일이 업로드 되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TempFileDto.class))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"파일 업로드 중 오류가 발생했습니다.\" }"))),
    })
    @PostMapping
    public SuccessResponse uploadFile(@RequestBody @Valid AttachFileCreateRequest request) {
        service.uploadFile(request);
        return SuccessResponse.success(SuccessCodes.FILE_UPLOAD_SUCCESS);
    }

    @Operation(summary = "delete file",
            description = "파일을 삭제한다.",
            parameters = @Parameter(
                    name = "fileId",
                    description = "파일 ID",
                    required = true,
                    in = ParameterIn.PATH,
                    schema = @Schema(type = "Long",
                            allowableValues = {}
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 되었습니다.", content = @Content(
                    mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"파일 삭제 중 오류가 발생했습니다.\" }"))),
    })
    @DeleteMapping("{fileId}")
    public SuccessResponse deleteFile(@PathVariable("fileId") Long fileId) {
        service.deleteFile(fileId);
        return SuccessResponse.success(SuccessCodes.DELETE_SUCCESS);
    }

    @Operation(summary = "delete temp file",
            description = "임시 저장된 파일을 삭제한다.",
            parameters = @Parameter(
                    name = "tempFileFullPath",
                    description = "임시 파일 경로",
                    required = true,
                    in = ParameterIn.QUERY,
                    example = "./UPLOAD/TEMP/FILE/0232b309-7b60-402e-8976-9aebb58f0d98.png"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 되었습니다.", content = @Content(
                    mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"파일 삭제 중 오류가 발생했습니다.\" }"))),
    })
    @DeleteMapping("{fileId}")
    public SuccessResponse deleteTempFile(@RequestParam("tempFileFullPath") String tempFileFullPath) {
        service.deleteTempFile(tempFileFullPath);
        return SuccessResponse.success(SuccessCodes.DELETE_SUCCESS);
    }
}
