package com.project.beauty_care.domain.board.repository;

import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.board.dto.BoardCriteria;
import com.project.beauty_care.domain.board.dto.AdminBoardCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardCustomRepository {
    Page<Board> findAllByCriteriaPage(BoardCriteria criteria, Pageable pageable);

    Page<Board> findAllByCriteriaAdmin(AdminBoardCriteria criteria, Pageable pageable);

//    Page<Board> findAllByCriteriaAndBoardReportsIsNotEmpty(BoardCriteria criteria, Pageable pageable);
}
