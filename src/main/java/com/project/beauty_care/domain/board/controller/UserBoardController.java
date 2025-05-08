package com.project.beauty_care.domain.board.controller;

import com.project.beauty_care.domain.board.dto.BoardCreateRequest;
import com.project.beauty_care.domain.board.dto.BoardResponse;
import com.project.beauty_care.domain.board.service.BoardService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import com.project.beauty_care.global.security.dto.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user/board")
@RestController
public class UserBoardController {
    private final BoardService service;

    @PostMapping
    public SuccessResponse<BoardResponse> createBoard(@RequestBody @Valid BoardCreateRequest request,
                                                      @AuthenticationPrincipal AppUser loginUser) {
        return SuccessResponse.success(SuccessCodes.SAVE_SUCCESS, service.createBoard(request, loginUser));
    }
}
