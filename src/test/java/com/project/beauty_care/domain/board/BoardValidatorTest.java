package com.project.beauty_care.domain.board;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.board.repository.BoardRepository;
import com.project.beauty_care.domain.enums.BoardType;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class BoardValidatorTest extends TestSupportWithOutRedis {
    @Autowired
    private BoardValidator validator;
    
    @MockitoBean
    private BoardRepository repository;

    @DisplayName("게시판 유형에 따른 작성 권한 검증")
    @ParameterizedTest(name = "[{index}] boardType={0}, role={1}, shouldThrow={2}")
    @CsvSource({
            "NOTIFICATION, USER, true",
            "NOTIFICATION, ADMIN, false",
            "FREE, USER, false"
    })
    void validBoardType(BoardType boardType, String role, boolean isException) {
        if (isException) {
            assertThatThrownBy(() -> validator.validBoardType(boardType, role))
                    .isInstanceOf(RequestInvalidException.class)
                    .extracting("errors")
                    .satisfies(errors -> {
                        assertErrors(errors, Errors.CAN_NOT_WRITE_BOARD_NOTIFICATION);
                    });
        } else {
            assertDoesNotThrow(() -> validator.validBoardType(boardType, role));
        }
    }

    @DisplayName("게시물 작성 시간 검증")
    @ParameterizedTest(name = "[{index}] role={0}, exists={1}, shouldThrow={2}")
    @MethodSource("com.project.beauty_care.RequestProviderFactory#boardRequestValidTime")
    void validCreatedDateTime(String role, boolean isExists, boolean isException) {
        // given
        when(repository.existsBoardByCreatedByAndCreatedDateTimeBetween(any(), any(), any()))
                .thenReturn(isExists);

        // when, then
        if (isException) {
            assertThatThrownBy(() -> validator.validCreatedDateTime(role, 1L))
                    .isInstanceOf(RequestInvalidException.class)
                    .extracting("errors")
                    .satisfies(errors -> {
                        assertErrors(errors, Errors.MUST_WRITE_BOARD_AFTER_ONE_MINUTE);
                    });
        } else {
            assertDoesNotThrow(() -> validator.validCreatedDateTime(role, 1L));
        }
    }

    private static void assertErrors(Object errors, Errors error) {
        assertThat(errors).hasFieldOrPropertyWithValue("message", error.getMessage());
        assertThat(errors).hasFieldOrPropertyWithValue("errorCode", error.getErrorCode());
    }
}