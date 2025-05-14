package com.project.beauty_care.domain.board.service;

import com.project.beauty_care.TestSupportWithOutRedis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private BoardService service;

    @DisplayName("게시물을 등록한다.")
    @Test
    void createBoard() {
    }

    @Test
    void updateBoard() {
    }

    @Test
    void findBoardById() {
    }

    @Test
    void findBoardAllPageByCriteria() {
    }
}