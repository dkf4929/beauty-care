package com.project.beauty_care.domain.board.repository;

import com.project.beauty_care.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository {
    Boolean existsBoardByCreatedByAndCreatedDateTimeBetween(Long createdMemberId, LocalDateTime createdDateTime, LocalDateTime now);
}
