package com.project.beauty_care.domain.board;

import com.project.beauty_care.RepositoryTestSupport;
import com.project.beauty_care.domain.board.dto.BoardCriteria;
import com.project.beauty_care.domain.board.repository.BoardRepository;
import com.project.beauty_care.domain.enums.BoardType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

// 테스트 메서드 사용을 위해 엔티티와 동일 패키지로 지정
class BoardRepositoryTest extends RepositoryTestSupport {
    @Autowired
    private BoardRepository repository;

    @DisplayName("특정 사용자가 특정 시간대에 작성한 게시물이 존재하는 지 확인")
    @TestFactory
    Collection<DynamicTest> existsBoardByCreatedByAndCreatedDateTimeBetween() {
        // given
        final Long createdMemberId = 1L;
        final LocalDateTime createdDateTime = LocalDateTime.now();

        Board board = buildEntity(BoardType.FREE, "제목", "내용");
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

    @DisplayName("조회 조건에 따른, 게시물 조회 case")
    @ParameterizedTest
    @Transactional
    @MethodSource("com.project.beauty_care.RequestProviderFactory#boardCriteriaRequestProvider")
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
        Board entity1 = buildEntity(BoardType.FREE, "내용1", "제목1");
        Board entity2 = buildEntity(BoardType.FREE, "내용2", "제목2");
        Board entity3 = buildEntity(BoardType.NOTIFICATION, "공지", "공지사항");

        entity1.setCreatedByForTest(1L);

        repository.saveAll(List.of(entity1, entity2, entity3));
    }

    private static Board buildEntity(BoardType type,
                                     String content,
                                     String title) {
        return Board.builder()
                .boardType(type)
                .title(title)
                .content(content)
                .isUse(true)
                .build();
    }

    private static void setBaseEntity(Board board, LocalDateTime now, Long createdMemberId) {
        board.setCreatedDateTimeForTest(now);
        board.setCreatedByForTest(createdMemberId);
    }
}