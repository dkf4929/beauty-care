package com.project.beauty_care.domain.boardReport.service;

import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.board.service.UserBoardService;
import com.project.beauty_care.domain.boardReport.BoardReport;
import com.project.beauty_care.domain.boardReport.BoardReportConverter;
import com.project.beauty_care.domain.boardReport.dto.BoardReportCreateRequest;
import com.project.beauty_care.domain.boardReport.repository.BoardReportRepository;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import com.project.beauty_care.global.security.dto.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardReportService {
    private final BoardReportConverter converter;
    private final MemberService memberService;
    private final UserBoardService userBoardService;
    private final BoardReportRepository repository;

    @Transactional
    public Long createBoardReport(AppUser loginUser, BoardReportCreateRequest request) {
        Member member = memberService.findMemberById(loginUser.getMemberId());
        Board board = userBoardService.findBoardById(request.getBoardId());

        BoardReport boardReport = converter.buildEntity(request.getReason(), member, board);

        return repository.save(boardReport).getId();
    }

    // hard-delete
    @Transactional
    public void deleteBoardReport(AppUser loginUser, Long boardId) {
        int deletedCount = repository.deleteByMemberIdAndBoardId(loginUser.getMemberId(), boardId);

        if (deletedCount == 0)
            throw new RequestInvalidException(Errors.NOT_FOUND_BOARD);
    }
}
