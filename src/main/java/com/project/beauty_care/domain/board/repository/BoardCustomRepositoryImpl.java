package com.project.beauty_care.domain.board.repository;

import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.board.QBoard;
import com.project.beauty_care.domain.board.dto.AdminBoardCriteria;
import com.project.beauty_care.domain.board.dto.BoardCriteria;
import com.project.beauty_care.domain.enums.BoardType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class BoardCustomRepositoryImpl implements BoardCustomRepository {
    private final JPAQueryFactory queryFactory;
    private final QBoard board;

    public BoardCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
        this.board = QBoard.board;
    }

    @Override
    public Page<Board> findAllByCriteriaAdmin(AdminBoardCriteria criteria, Pageable pageable) {
        OrderSpecifier<?> orderSpecifier = sortIsReport(criteria.getIsReport());

        JPAQuery<Board> query = queryFactory
                .selectFrom(board)
                .where(
                        isContainReported(criteria.getIsReport()),
                        eqBoardType(criteria.getBoardType()),
                        containsTitle(criteria.getTitle()),
                        containsContent(criteria.getContent()),
                        eqGrade(criteria.getGrade()),
                        eqIsUse(criteria.getIsUse()),
                        eqCreatedBy(criteria.getCreatedBy())
                );

        // 신고 여부 true => 신고 횟수로 descending
        if (orderSpecifier != null) query.orderBy(orderSpecifier);

        List<Board> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .where(
                        eqBoardType(criteria.getBoardType()),
                        containsTitle(criteria.getTitle()),
                        containsContent(criteria.getContent()),
                        eqGrade(criteria.getGrade()),
                        eqCreatedBy(criteria.getCreatedBy())
                ).fetchOne();

        return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0L);
    }

    private OrderSpecifier<?> sortIsReport(Boolean isReport) {
        if (isReport) return board.boardReports.size().desc();

        return null;
    }

    @Override
    public Page<Board> findAllByCriteriaPage(BoardCriteria criteria, Pageable pageable) {
        List<Board> content = queryFactory
                .selectFrom(board)
                .where(
                        eqBoardType(criteria.getBoardType()),
                        containsTitle(criteria.getTitle()),
                        containsContent(criteria.getContent()),
                        eqGrade(criteria.getGrade()),
                        eqCreatedBy(criteria.getCreatedBy()),
                        board.isUse.isTrue() // 숨김 처리 안된 게시물만
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .where(
                        eqBoardType(criteria.getBoardType()),
                        containsTitle(criteria.getTitle()),
                        containsContent(criteria.getContent()),
                        eqGrade(criteria.getGrade()),
                        eqCreatedBy(criteria.getCreatedBy())
                ).fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression eqCreatedBy(Long createdBy) {
        if (createdBy == null) return null;

        return board.createdBy.eq(createdBy);
    }

    private BooleanExpression eqGrade(String grade) {
        if (StringUtils.isEmpty(grade)) return null;

        return board.grade.id.eq(grade);
    }

    private BooleanExpression containsContent(String content) {
        if (StringUtils.isEmpty(content)) return null;

        return board.content.contains(content);
    }

    private BooleanExpression containsTitle(String title) {
        if (StringUtils.isEmpty(title)) return null;

        return board.title.contains(title);
    }

    private BooleanExpression eqBoardType(BoardType boardType) {
        if (boardType == null) return null;

        return board.boardType.eq(boardType);
    }

    private BooleanExpression isContainReported(Boolean isReport) {
        if (!isReport) return board.boardReports.isEmpty();

        return board.boardReports.isNotEmpty();
    }

    private BooleanExpression eqIsUse(Boolean isUse) {
        if (isUse == null) return null;

        return board.isUse.eq(isUse);
    }
}
