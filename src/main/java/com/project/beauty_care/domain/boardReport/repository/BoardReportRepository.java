package com.project.beauty_care.domain.boardReport.repository;

import com.project.beauty_care.domain.boardReport.BoardReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardReportRepository extends JpaRepository<BoardReport, Long> {
    @Modifying
    @Query("DELETE FROM BoardReport br WHERE br.reportMember.id = :memberId AND br.board.id = :boardId")
    int deleteByMemberIdAndBoardId(@Param("memberId") Long memberId, @Param("boardId") Long boardId);
}
