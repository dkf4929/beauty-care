package com.project.beauty_care.domain.board.service;

import com.project.beauty_care.domain.board.dto.AdminBoardResponse;
import com.project.beauty_care.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminBoardService {
    private final BoardRepository repository;

    // 신고 게시물 보기
    public Page<AdminBoardResponse> findReportedBoards(Pageable pageable) {
        return null;
    }

    // hard-delete
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    // 전체 게시물 조회
    public Page<AdminBoardResponse> findAllBoards(Pageable pageable) {
        return null;
    }
}
