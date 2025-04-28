package com.project.beauty_care.domain.member.controller;

import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.dto.UserMemberUpdateRequest;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import com.project.beauty_care.global.security.dto.AppUser;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "MEMBER REST API FOR USER", description = "사용자 API")
@RequestMapping("/user/member")
@RequiredArgsConstructor
@RestController
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
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E004\", \"message\": \"해당 API를 호출할 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @GetMapping
    public SuccessResponse<MemberResponse> findMemberById(@AuthenticationPrincipal AppUser appUser) {
        MemberResponse response = service.findMemberById(appUser.getMemberId());
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, response);
    }

    @Operation(summary = "사용자 수정",
            description = "사용자 정보를 수정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청값 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"Request Invalid Message\" }"))),
            @ApiResponse(responseCode = "401", description = "로그인 인증 에러", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E003\",\"message\": \"로그인 후 진행하세요.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"code\": \"E007\", \"message\": \"서버에 오류가 발생했습니다. 관리자에게 문의하세요.\" }"))),
    })
    @PutMapping
    public SuccessResponse<MemberResponse> updateMember(@RequestBody @Valid UserMemberUpdateRequest request) {
        MemberResponse response = service.updateMemberUser(request);
        return SuccessResponse.success(SuccessCodes.UPDATE_SUCCESS, response);
    }

    @Operation(summary = "회원 탈퇴", description = "서비스를 탈퇴한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "탈퇴 완료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Long.class, example = "1"))),
            @ApiResponse(responseCode = "400", description = "관리자 계정 삭제 불가", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"관리자 권한을 가진 사용자는 서비스 탈퇴가 불가능 합니다. 시스템 관리자에게 문의 하세요.\" }"))),
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
    @DeleteMapping
    public SuccessResponse<Long> deleteMember(@AuthenticationPrincipal AppUser appUser) {
        return SuccessResponse.success(
                SuccessCodes.CANCEL_SUCCESS, service.softDeleteMember(appUser, LocalDate.now()));
    }

    @Operation(summary = "회원 탈퇴 취소", description = "서비스를 탈퇴를 취소한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "탈퇴 취소 되었습니다.", content = @Content(
                    mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "이미 탈퇴 처리된 회원", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"code\": \"E006\",\"message\": \"탈퇴 신청한지 2주가 지난 회원입니다.\" }"))),
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
    @PatchMapping
    public SuccessResponse<Object> deleteCancelMember(@AuthenticationPrincipal AppUser appUser) {
        service.deleteCancel(appUser, LocalDate.now());
        return SuccessResponse.success(SuccessCodes.CANCEL_SUCCESS, new Object());
    }
}
