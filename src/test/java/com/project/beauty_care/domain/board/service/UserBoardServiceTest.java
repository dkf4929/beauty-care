package com.project.beauty_care.domain.board.service;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.board.BoardValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

class UserBoardServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private UserBoardService service;

    @MockitoBean
    private BoardValidator validator;

    @DisplayName("게시물을 등록한다.")
    @Test
    void createBoard() {
        doNothing().when(validator).validCreatedDateTime(any(), any());
        doNothing().when(validator).validBoardType(any(), any());
    }

    @Test
    void updateBoard() {
    }

    @Test
    void findBoardByIdAndConvertResponse() {
    }

    @Test
    void findBoardAllPageByCriteria() {
    }
}