package com.project.beauty_care.domain.member.controller;

import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "MEMBER REST API(FOR ADMIN)", description = "사용자 API")
@RequestMapping("/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {
    private final MemberService service;

    @GetMapping
    public SuccessResponse<List<MemberResponse>> findAllMembers() {
        List<MemberResponse> members = service.findAllMembers();
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, HttpStatus.OK, members);
    }
}
