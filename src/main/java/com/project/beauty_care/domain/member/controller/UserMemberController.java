package com.project.beauty_care.domain.member.controller;

import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import com.project.beauty_care.global.security.dto.AppUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/member")
public class UserMemberController {
    private final MemberService service;

    @Operation(summary = "myInfo",
            description = "로그인한 사용자의 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회가 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "400", description = "Entity Not Found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\", \"message\": \"가입된 회원이 아닙니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @GetMapping
    public SuccessResponse<MemberResponse> findMemberById(@AuthenticationPrincipal AppUser appUser) {
        MemberResponse response = service.findMemberById(appUser.getMemberId());
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, HttpStatus.OK, response);
    }
}
