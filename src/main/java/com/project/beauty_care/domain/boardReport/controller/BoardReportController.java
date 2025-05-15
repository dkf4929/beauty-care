package com.project.beauty_care.domain.boardReport.controller;

import com.project.beauty_care.domain.boardReport.BoardReport;
import com.project.beauty_care.domain.boardReport.dto.BoardReportCreateRequest;
import com.project.beauty_care.domain.boardReport.service.BoardReportService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import com.project.beauty_care.global.security.dto.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/board-report")
@RequiredArgsConstructor
public class BoardReportController {
    private final BoardReportService service;


    @PostMapping
    public SuccessResponse<Long> createBoardReport(@AuthenticationPrincipal AppUser loginUser,
                                                   @RequestBody BoardReportCreateRequest request) {
        return SuccessResponse.success(SuccessCodes.REPORT_SUCCESS, service.createBoardReport(loginUser, request));
    }

    @DeleteMapping
    public SuccessResponse deleteBoardReport(@AuthenticationPrincipal AppUser loginUser,
                                             @RequestParam("boardId") Long boardId) {
        service.deleteBoardReport(loginUser, boardId);

        return SuccessResponse.success(SuccessCodes.REPORT_CANCEL);
    }
}
