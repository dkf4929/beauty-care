package com.project.beauty_care.domain.boardReport;

import com.project.beauty_care.domain.BaseTimeEntity;
import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 한 게시물에 대한 신고 한번만.
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UQ_BOARD_ID_AND_MEMBER_ID", columnNames = {"board_id", "member_id"})
})
public class BoardReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member reportMember;

    @NotBlank
    private String reason;

    @Builder
    public BoardReport(Board board, Member reportMember, String reason) {
        this.board = board;
        this.reportMember = reportMember;
        this.reason = reason;
    }
}
