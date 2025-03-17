package com.project.beauty_care.domain.member.controller;

import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.MemberCreateRequest;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "MEMBER REST API", description = "사용자 API")
@RequestMapping("/public/member")
public class PublicMemberController {
    private final MemberService service;

    @Operation(summary = "회원가입", description = "사용자 정보를 입력하여 회원가입 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 완료", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Long.class, example = "1"))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "409", description = "Duplicated", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E005\", \"message\": \"이미 존재하는 아이디 입니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"result\": \"1\", \"code\": \"E007\", \"message\": \"INTERNAL SERVER ERROR\" }"))),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> createMember(@Valid @RequestBody MemberCreateRequest request) {
        Member savedMember = service.createMember(request);
        return SuccessResponse.success(SuccessCodes.MEMBER_SAVE_SUCCESS, HttpStatus.CREATED, savedMember.getId());
    }
}
