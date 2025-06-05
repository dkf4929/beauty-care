package com.project.beauty_care.domain.board.controller;

import com.project.beauty_care.domain.board.dto.AdminBoardCriteria;
import com.project.beauty_care.domain.board.dto.AdminBoardResponse;
import com.project.beauty_care.domain.board.service.AdminBoardService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/board")
public class AdminBoardController {
    private final AdminBoardService service;

    @Operation(summary = "게시물 조회 (FOR ADMIN)",
            description = "조건에 일치하는 모든 게시물을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 조회 완료 되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AdminBoardResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Request Invalid",
                                    value = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }")
                    }
            )
            ),
            @ApiResponse(responseCode = "401", description = "로그인 인증 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\",\"message\": \"로그인 후 진행하세요.\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content =
            @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "API Authority Error",
                                    value = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"),
                    }
            )),
            @ApiResponse(responseCode = "404", description = "ENTITY NOT FOUND", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\", \"message\": \"등록된 게시물이 아닙니다.\" }"))),
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
    public SuccessResponse<Page<AdminBoardResponse>> findAllByCriteria(Pageable pageable,
                                                                                @RequestBody AdminBoardCriteria criteria) {
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, service.findAllBoards(pageable, criteria));
    }
}
