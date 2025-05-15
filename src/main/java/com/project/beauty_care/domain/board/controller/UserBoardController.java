package com.project.beauty_care.domain.board.controller;

import com.project.beauty_care.domain.board.dto.BoardCreateRequest;
import com.project.beauty_care.domain.board.dto.BoardCriteria;
import com.project.beauty_care.domain.board.dto.BoardResponse;
import com.project.beauty_care.domain.board.service.UserBoardService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import com.project.beauty_care.global.security.dto.AppUser;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/user/board")
@Tag(name = "BOARD REST API FOR USER", description = "게시판 API")
@RestController
public class UserBoardController {
    private final UserBoardService service;

    @Operation(summary = "게시물 조회",
            description = "게시물 정보를 조회합니다.",
            parameters = @Parameter(
                    name = "boardId",
                    description = "게시물 ID",
                    required = true,
                    in = ParameterIn.PATH,
                    schema = @Schema(type = "integer")
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회가 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BoardResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "400", description = "Entity Not Found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\", \"message\": \"게시물을 찾을 수 없습니다.\" }"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @GetMapping("{boardId}")
    public SuccessResponse<BoardResponse> findBoardById(@PathVariable("boardId") Long boardId,
                                                        @AuthenticationPrincipal AppUser loginUser) {
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, service.findBoardByIdAndConvertResponse(boardId, loginUser));
    }

    @Operation(summary = "게시물 등록",
            description = "게시물을 등록한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 저장이 완료 되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BoardResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Request Invalid",
                                    value = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"),
                            @ExampleObject(name = "Should Write One Minute After",
                                    value = "{ \"code\": \"E006\",\"message\": \"게시물을 작성한 지 1분 이내에 새로운 게시물을 작성할 수 없습니다.\" }"),
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
                            @ExampleObject(name = "Authentication Error",
                                    value = "{ \"code\": \"E004\", \"message\": \"공지 작성은 관리자만 가능합니다.\" }")
                    }
            )),
            @ApiResponse(responseCode = "404", description = "ENTITY NOT FOUND", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\", \"message\": \"등록된 코드가 아닙니다.\" }"))),
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
    public SuccessResponse<BoardResponse> createBoard(@RequestBody @Valid BoardCreateRequest request,
                                                      @AuthenticationPrincipal AppUser loginUser) {
        return SuccessResponse.success(SuccessCodes.SAVE_SUCCESS, service.createBoard(request, loginUser));
    }

    @Operation(summary = "게시물 조회 By Criteria",
            description = "조건과 일치하는 게시물 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회가 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BoardResponse.class))),
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
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @PostMapping("/criteria")
    public SuccessResponse<Page<BoardResponse>> findBoardByCriteria(@RequestBody BoardCriteria criteria, Pageable pageable) {
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, service.findBoardAllPageByCriteria(criteria, pageable));
    }
}
