package com.project.beauty_care.domain.member;

import com.project.beauty_care.domain.member.dto.MemberCreateRequest;
import com.project.beauty_care.global.ApiRs;
import com.project.beauty_care.global.enums.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "MEMBER REST API", description = "사용자 API")
@RequestMapping("/member")
public class MemberController {
    private final MemberService service;

    @Operation(summary = "회원가입", description = "사용자 정보를 입력하여 회원가입 합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"result\": \"1\", \"code\": \"4002\",\"message\": \"올바른 입력값을 입력하세요.\" }"))),
            @ApiResponse(responseCode = "401", description = "PASSWORD MISS MATCH", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"result\": \"1\", \"code\": \"1002\", \"message\": \"비밀번호가 일치하지 않습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "ENTITY NOT FOUND", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"result\": \"1\", \"code\": \"2001\", \"message\": \"등록된 회원이 아닙니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"result\": \"1\", \"code\": \"9999\", \"message\": \"INTERNAL SERVER ERROR\" }"))),
    })
    @PostMapping
    public ApiRs<Long> createMember(@Valid @RequestBody MemberCreateRequest request) {
        Member savedMember = service.createMember(request);
        return ApiRs.success(SuccessResult.MEMBER_SAVE_SUCCESS, HttpStatus.OK, savedMember.getId());
    }
}
