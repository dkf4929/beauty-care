package com.project.beauty_care.domain.role.controller;

import com.project.beauty_care.domain.role.dto.RoleCreateRequest;
import com.project.beauty_care.domain.role.dto.RoleMemberResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.dto.RoleUpdateRequest;
import com.project.beauty_care.domain.role.service.RoleService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ROLE REST API", description = "Role(API 권한) 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/role")
public class AdminRoleController {
    private final RoleService service;

    @Operation(summary = "권한 목록 조회",
            description = "조건에 맞는 권한 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회가 완료 되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RoleMemberResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 인증 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\",\"message\": \"로그인 후 진행하세요.\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
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
    @GetMapping
    public SuccessResponse<List<RoleMemberResponse>> findAllRoles(@RequestParam(name = "roleName", required = false) String roleName) {
        List<RoleMemberResponse> roles = service.findAllRoles(roleName);
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, roles);
    }

    @Operation(summary = "권한 생성",
            description = "API 호출 권한을 생성한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "권한 생성 완료 되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RoleMemberResponse.class))),
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
            @ApiResponse(responseCode = "409", description = "중복된 권한", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E005\",\"message\": \"중복된 권한이 존재합니다.\" }"))),
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
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<RoleResponse> createRole(@RequestBody @Valid RoleCreateRequest request) {
        RoleResponse response = service.createRole(request);
        return SuccessResponse.success(SuccessCodes.SAVE_SUCCESS, response);
    }

    @Operation(summary = "권한 수정",
            description = "API 호출 권한을 수정한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료 되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RoleMemberResponse.class))),
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
            @ApiResponse(responseCode = "409", description = "중복된 권한", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E005\",\"message\": \"중복된 권한이 존재합니다.\" }"))),
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
    @PutMapping
    public SuccessResponse<RoleResponse> updateRole(@RequestBody @Valid RoleUpdateRequest request) {
        RoleResponse response = service.updateRole(request);
        return SuccessResponse.success(SuccessCodes.UPDATE_SUCCESS, response);
    }

    @Operation(summary = "권한 삭제",
            description = "API 호출 권한을 삭제한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료 되었습니다.", content = @Content(
                    mediaType = "application/json")),
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
    @DeleteMapping("/{role}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse hardDeleteRole(@PathVariable("role") String role) {
        service.hardDeleteRole(role);
        return SuccessResponse.success(SuccessCodes.DELETE_SUCCESS, HttpStatus.NO_CONTENT);
    }
}
