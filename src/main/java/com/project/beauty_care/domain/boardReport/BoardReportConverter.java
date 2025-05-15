package com.project.beauty_care.domain.boardReport;

import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.member.Member;
import org.springframework.stereotype.Component;

@Component
public class BoardReportConverter {
    public BoardReport buildEntity(String reason, Member reportMember, Board board) {
        return BoardReport.builder()
                .board(board)
                .reason(reason)
                .reportMember(reportMember)
                .build();
    }
}
