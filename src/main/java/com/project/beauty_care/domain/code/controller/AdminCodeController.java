package com.project.beauty_care.domain.code.controller;

import com.project.beauty_care.domain.code.dto.AdminCodeCreateRequest;
import com.project.beauty_care.domain.code.dto.AdminCodeUpdateRequest;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.code.service.CodeService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "CODE REST API FOR ADMIN", description = "코드 API(ADMIN)")
@RequestMapping("/admin/code")
public class AdminCodeController {
    private final CodeService service;

    @Operation(summary = "코드 조회(상세)",
            description = "특정 코드를 조회한다.",
            parameters = @Parameter(
                    name = "codeId",
                    description = "코드 ID",
                    required = true,
                    in = ParameterIn.PATH,
                    schema = @Schema(type = "string",
                            allowableValues = {}
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CodeResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 인증 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\",\"message\": \"로그인 후 진행하세요.\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "ENTITY NOT FOUND", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\", \"message\": \"등록된 코드가 아닙니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @GetMapping("/{codeId}")
    public SuccessResponse<CodeResponse> findCodeById(@PathVariable("codeId") String codeId) {
        CodeResponse code =  service.findCodeByIdCache(codeId);
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, code);
    }

    @Operation(summary = "코드 조회",
            description = "등록된 모든 코드를 계층 형태로 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CodeResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 인증 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\",\"message\": \"로그인 후 진행하세요.\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @GetMapping
    public SuccessResponse<CodeResponse> findAllCode() {
        CodeResponse code =  service.findAllCode();
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, code);
    }

    @Operation(summary = "코드 등록",
            description = "코드를 등록한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코드 저장이 완료 되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "401", description = "로그인 인증 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\",\"message\": \"로그인 후 진행하세요.\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "ENTITY NOT FOUND", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\", \"message\": \"등록된 코드가 아닙니다.\" }"))),
            @ApiResponse(responseCode = "409", description = "Duplicated", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E005\", \"message\": \"동일한 레벨의 코드에 중복되는 명칭이 존재합니다.\" }"))),
            @ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL SERVER ERROR",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Server Error",
                                                    value = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }")
                                    }
                            )
                    }
            )
    })
    @PostMapping
    public SuccessResponse<CodeResponse> createCode(@RequestBody @Valid AdminCodeCreateRequest request) {
        CodeResponse response = service.createCode(request);
        return SuccessResponse.success(SuccessCodes.SAVE_SUCCESS, response);
    }

    @Operation(summary = "코드 수정",
            description = "코드를 수정한다.",
            parameters = @Parameter(
                    name = "codeId",
                    description = "코드 ID",
                    required = true,
                    in = ParameterIn.PATH,
                    schema = @Schema(type = "string",
                            allowableValues = {}
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CodeResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 인증 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\",\"message\": \"로그인 후 진행하세요.\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "ENTITY NOT FOUND", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\", \"message\": \"등록된 코드가 아닙니다.\" }"))),
            @ApiResponse(responseCode = "409", description = "Duplicated", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E005\", \"message\": \"동일한 레벨의 코드에 중복되는 명칭이 존재합니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @PutMapping("/{codeId}")
    public SuccessResponse<CodeResponse> updateCode(@PathVariable("codeId") String codeId,
                                                    @RequestBody @Valid AdminCodeUpdateRequest request) {
        CodeResponse response = service.updateCode(codeId, request);
        return SuccessResponse.success(SuccessCodes.UPDATE_SUCCESS, response);
    }

    @Operation(summary = "코드 삭제",
            description = "코드를 삭제한다.",
            parameters = @Parameter(
                    name = "codeId",
                    description = "코드 ID",
                    required = true,
                    in = ParameterIn.PATH,
                    schema = @Schema(type = "string",
                            allowableValues = {}
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class, example = "sys:agree:Y"))),
            @ApiResponse(responseCode = "400", description = "하위코드 존재", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"하위코드가 존재하는 경우, 삭제할 수 없습니다.\" }"))),
            @ApiResponse(responseCode = "401", description = "로그인 인증 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\",\"message\": \"로그인 후 진행하세요.\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "ENTITY NOT FOUND", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\", \"message\": \"등록된 코드가 아닙니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @DeleteMapping("/{codeId}")
    public SuccessResponse<String> deleteCode(@PathVariable("codeId") String codeId) {
        String deleteId = service.deleteCode(codeId);
        return SuccessResponse.success(SuccessCodes.UPDATE_SUCCESS, deleteId);
    }
}
