package com.project.beauty_care.domain.member;

import com.project.beauty_care.domain.member.dto.MemberCreateRequest;
import com.project.beauty_care.domain.member.dto.MemberResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "MEMBER REST API", description = "사용자 API")
@RequestMapping("/member")
public class MemberController {
    private final MemberService service;

    @Operation(summary = "회원가입", description = "사용자 정보를 입력하여 회원가입 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 완료", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"message\": \"회원가입이 완료 되었습니다.\", \"code\": \"M001\",\"data\": \"1\" }"))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"R001\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "409", description = "Duplicated", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"D001\", \"message\": \"이미 존재하는 아이디 입니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"result\": \"1\", \"code\": \"9999\", \"message\": \"INTERNAL SERVER ERROR\" }"))),
    })
    @PostMapping
    public ApiRs<Long> createMember(@Valid @RequestBody MemberCreateRequest request) {
        Member savedMember = service.createMember(request);
        return ApiRs.success(SuccessResult.MEMBER_SAVE_SUCCESS, HttpStatus.OK, savedMember.getId());
    }

    @GetMapping
    public ApiRs<List<MemberResponse>> findAllMembers() {
        List<MemberResponse> members = service.findAllMembers();
        return ApiRs.success(SuccessResult.RETRIEVE_SUCCESS, HttpStatus.OK, members);
    }
}
