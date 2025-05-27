package com.project.beauty_care.domain.board;

import com.project.beauty_care.RepositoryTestSupport;
import com.project.beauty_care.domain.board.dto.BoardCriteria;
import com.project.beauty_care.domain.board.dto.AdminBoardCriteria;
import com.project.beauty_care.domain.board.repository.BoardRepository;
import com.project.beauty_care.domain.boardReport.BoardReport;
import com.project.beauty_care.domain.boardReport.repository.BoardReportRepository;
import com.project.beauty_care.domain.enums.BoardType;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

// 테스트 메서드 사용을 위해 엔티티와 동일 패키지로 지정
class BoardRepositoryTest extends RepositoryTestSupport {
    @Autowired
    private BoardRepository repository;

    @Autowired
    private BoardReportRepository boardReportRepository;

    @DisplayName("특정 사용자가 특정 시간대에 작성한 게시물이 존재하는 지 확인")
    @TestFactory
    Collection<DynamicTest> existsBoardByCreatedByAndCreatedDateTimeBetween() {
        // given
        final Long createdMemberId = 1L;
        final LocalDateTime createdDateTime = LocalDateTime.now();

        Board board = buildEntity(BoardType.FREE, "제목", "내용", Boolean.TRUE);
        setBaseEntity(board, createdDateTime, createdMemberId);

        repository.save(board);

        return List.of(
                dynamicTest("exist", () -> {
                    // when
                    boolean exists = repository.existsBoardByCreatedByAndCreatedDateTimeBetween(
                            createdMemberId,
                            createdDateTime,
                            createdDateTime.plusSeconds(1) // + 1초
                    );

                    // then
                    assertThat(exists).isTrue();
                }),
                dynamicTest("not exist", () -> {
                    // when
                    boolean exists = repository.existsBoardByCreatedByAndCreatedDateTimeBetween(
                            createdMemberId,
                            createdDateTime.minusSeconds(2),
                            createdDateTime.minusSeconds(1)
                    );

                    // then
                    assertThat(exists).isFalse();
                })
        );
    }

    @DisplayName("게시물 조회 FOR ADMIN")
    @Transactional
    @TestFactory
    Collection<DynamicTest> findAllByCriteriaPage() {
        // given
        saveEntity();

        AdminBoardCriteria criteriaWithNotReport
                = buildCriteria(BoardType.FREE, Boolean.TRUE, "제목", "내용", Boolean.FALSE, 1L);

        AdminBoardCriteria criteriaWithReport
                = buildCriteria(null, null, "", "", Boolean.TRUE, null);

        PageRequest pageRequest = PageRequest.of(0, 10);

        return List.of(
                dynamicTest("특정 게시물 조회", () -> {
                    final int expectedSize = 1;

                    // when
                    Page<Board> pageResult = repository.findAllByCriteriaAdmin(criteriaWithNotReport, pageRequest);
                    List<Board> contentList = pageResult.getContent();

                    // then
                    assertThat(contentList.size()).isEqualTo(expectedSize);
                    assertThat(contentList)
                            .extracting(board -> board.getBoardType(),
                                    board -> board.getIsUse(),
                                    board -> board.getBoardReports().size(),
                                    board -> board.getCreatedBy())
                            .containsExactly(tuple(
                                    BoardType.FREE,
                                    Boolean.TRUE,
                                    0,
                                    1L)
                            );
                }),
                dynamicTest("신고 게시물 조회", () -> {
                    final int expectedSize = 2;

                    // when
                    Page<Board> pageResult = repository.findAllByCriteriaAdmin(criteriaWithReport, pageRequest);
                    List<Board> contentList = pageResult.getContent();

                    // then
                    assertThat(contentList.size()).isEqualTo(expectedSize);

                    // 신고 카운트 sorting
                    assertThat(contentList)
                            .extracting(board -> board.getBoardReports().size())
                            .containsExactly(3, 1);
                })
        );
    }

    @DisplayName("게시물 조회 FOR USER")
    @ParameterizedTest
    @Transactional
    @MethodSource("com.project.beauty_care.RequestProviderFactory#boardCriteria")
    void findAllByCriteriaPage(BoardCriteria criteria, int expected) {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);

        saveEntity();

        // when
        Page<Board> pageResult = repository.findAllByCriteriaPage(criteria, pageRequest);

        // then
        assertThat(pageResult.getTotalElements())
                .isEqualTo(expected);
    }

    private void saveEntity() {
        Board entity1 = buildEntity(BoardType.FREE, "내용1", "제목1", Boolean.TRUE);
        Board entity2 = buildEntity(BoardType.FREE, "내용2", "제목2", Boolean.TRUE);
        Board entity3 = buildEntity(BoardType.NOTIFICATION, "공지", "공지사항", Boolean.TRUE);
        Board entity4 = buildEntity(BoardType.FREE, "내용3", "제목3", Boolean.TRUE);
        Board entity5 = buildEntity(BoardType.FREE, "내용4", "제목4", Boolean.FALSE);

        entity1.setCreatedByForTest(1L);

        // 신고 게시글
        // entity4 -> 신고 카운트 3
        // entity5 -> 신고 카운트 1
        BoardReport boardReport1 = BoardReport.builder()
                .board(entity4)
                .reason("비속어 사용")
                .build();

        BoardReport boardReport2 = BoardReport.builder()
                .board(entity4)
                .reason("비속어 사용")
                .build();

        BoardReport boardReport3 = BoardReport.builder()
                .board(entity4)
                .reason("비속어 사용")
                .build();

        entity4.getBoardReports().addAll(List.of(boardReport1, boardReport2, boardReport3));

        BoardReport boardReport4 = BoardReport.builder()
                .board(entity5)
                .reason("광고글")
                .build();

        entity5.getBoardReports().add(boardReport4);

        repository.saveAll(List.of(entity1, entity2, entity3, entity4, entity5));
        boardReportRepository.saveAll(List.of(boardReport1, boardReport2, boardReport3, boardReport4));
    }

    private static Board buildEntity(BoardType type,
                                     String content,
                                     String title,
                                     Boolean isUse) {
        return Board.builder()
                .boardType(type)
                .title(title)
                .content(content)
                .isUse(isUse)
                .build();
    }

    private AdminBoardCriteria buildCriteria(BoardType type,
                                             Boolean isUse,
                                             String title,
                                             String content,
                                             Boolean isReport,
                                             Long createdBy) {
        return AdminBoardCriteria.builder()
                .boardType(type)
                .isUse(isUse)
                .title(title)
                .content(content)
                .isReport(isReport)
                .createdBy(createdBy)
                .build();
    }

    private static void setBaseEntity(Board board, LocalDateTime now, Long createdMemberId) {
        board.setCreatedDateTimeForTest(now);
        board.setCreatedByForTest(createdMemberId);
    }
}