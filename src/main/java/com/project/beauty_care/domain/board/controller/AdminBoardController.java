package com.project.beauty_care.domain.board.controller;

import com.project.beauty_care.domain.board.dto.AdminBoardResponse;
import com.project.beauty_care.domain.board.dto.AdminBoardCriteria;
import com.project.beauty_care.domain.board.service.AdminBoardService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
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

    @PostMapping
    public SuccessResponse<Page<AdminBoardResponse>> findAllByCriteria(Pageable pageable,
                                                                                @RequestBody AdminBoardCriteria criteria) {
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, service.findAllBoards(pageable, criteria));
    }
}
