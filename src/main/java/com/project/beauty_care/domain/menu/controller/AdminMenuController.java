package com.project.beauty_care.domain.menu.controller;

import com.project.beauty_care.domain.menu.dto.AdminMenuCreateRequest;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.menu.dto.AdminMenuUpdateRequest;
import com.project.beauty_care.domain.menu.service.MenuService;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MENU REST API FOR ADMIN", description = "메뉴 관리")
@RestController
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class AdminMenuController {
    private final MenuService service;

    @Operation(summary = "메뉴 등록",
            description = "메뉴를 등록한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 저장이 완료 되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AdminMenuResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Invalid Field",
                                    value = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"),
                            @ExampleObject(name = "Menu Depth Error",
                                    value = "{ \"code\": \"E006\", \"message\": \"메뉴는 최대 3depth 입니다.\" }")
                    })),
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
                    examples = {
                            @ExampleObject(name = "Not Found Role",
                                    value = "{ \"code\": \"E006\", \"message\": \"등록된 권한이 아닙니다.\" }"),
                            @ExampleObject(name = "Not Used Menu",
                                    value = "{ \"code\": \"E006\", \"message\": \"상위 메뉴를 찾을 수 없습니다.\" }"),
                            @ExampleObject(name = "Menu Max Level Error",
                                    value = "{ \"code\": \"E006\", \"message\": \"메뉴는 최대 3depth 입니다.\" }")
                    })
            ),
            @ApiResponse(responseCode = "409", description = "Duplicated", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Duplicated Menu Name",
                                    value = "{ \"code\": \"E005\", \"message\": \"중복된 메뉴명이 존재합니다.\" }"),
                            @ExampleObject(name = "Duplicated Menu Path",
                                    value = "{ \"code\": \"E005\", \"message\": \"중복된 메뉴 경로가 존재합니다.\" }")
                    }
            )),
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
    public SuccessResponse<AdminMenuResponse> createMenu(@RequestBody @Valid AdminMenuCreateRequest request) {
        return SuccessResponse.success(SuccessCodes.SAVE_SUCCESS, HttpStatus.CREATED, service.createMenu(request));
    }

    @Operation(summary = "메뉴 조회",
            description = "등록된 모든 메뉴를 계층 형태로 조회한다.",
            parameters = @Parameter(
                    name = "role",
                    description = "권한",
                    required = false,
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "string"),
                    example = "ADMIN"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AdminMenuResponse.class))),
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
    public SuccessResponse<AdminMenuResponse> findMenuAll(@RequestParam(required = false, value = "role") String role) {
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, HttpStatus.OK, service.findAllMenu(role));
    }

    @Operation(summary = "메뉴 수정",
            description = "메뉴를 수정한다.",
            parameters = @Parameter(
                    name = "menuId",
                    description = "메뉴 ID",
                    required = true,
                    in = ParameterIn.PATH,
                    schema = @Schema(type = "Long")
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AdminMenuResponse.class))),
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
                    examples = {
                            @ExampleObject(name = "Not Found Role",
                                    value = "{ \"code\": \"E006\", \"message\": \"등록된 권한이 아닙니다.\" }"),
                            @ExampleObject(name = "Not Used Menu",
                                    value = "{ \"code\": \"E006\", \"message\": \"상위 메뉴를 찾을 수 없습니다.\" }"),
                            @ExampleObject(name = "Menu Max Level Error",
                                    value = "{ \"code\": \"E006\", \"message\": \"메뉴는 최대 3depth 입니다.\" }")
                    })
            ),
            @ApiResponse(responseCode = "409", description = "Duplicated", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Duplicated Menu Name",
                                    value = "{ \"code\": \"E005\", \"message\": \"중복된 메뉴명이 존재합니다.\" }"),
                            @ExampleObject(name = "Duplicated Menu Path",
                                    value = "{ \"code\": \"E005\", \"message\": \"중복된 메뉴 경로가 존재합니다.\" }")
                    }
            )),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @PutMapping("/{menuId}")
    public SuccessResponse<AdminMenuResponse> updateCode(@PathVariable("menuId") Long menuId,
                                                         @RequestBody @Valid AdminMenuUpdateRequest request) {
        return SuccessResponse.success(SuccessCodes.UPDATE_SUCCESS, HttpStatus.OK, service.updateMenu(request, menuId));
    }

}
