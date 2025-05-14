package com.project.beauty_care.domain.board;

import com.google.common.annotations.VisibleForTesting;
import com.project.beauty_care.domain.BaseEntity;
import com.project.beauty_care.domain.attachFile.AttachFile;
import com.project.beauty_care.domain.board.dto.BoardUpdateRequest;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.enums.BoardType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_code_id")
    private Code grade;

    @NotBlank
    private String title;

    @NotBlank
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @OneToMany(mappedBy = "mappedId", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("mapped_entity = 'BOARD'")
    @BatchSize(size = 10)
    private List<AttachFile> attachFiles = new ArrayList<>();

    @ColumnDefault("0")
    private Integer readCount;

    @ColumnDefault("true")
    private Boolean isUse;

    @Builder
    public Board(BoardType boardType,
                 Code grade,
                 String title,
                 String content,
                 Boolean isUse) {
        this.boardType = boardType;
        this.grade = grade;
        this.title = title;
        this.content = content;
        this.isUse = isUse;
        this.readCount = 0;
    }

    public void updateReadCount(int readCount) {
        this.readCount = readCount;
    }

    public void updateBoard(BoardUpdateRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
    }

    // 테스트용 메서드 호출 x
    @VisibleForTesting
    void setCreatedDateTimeForTest(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    // 테스트용 메서드 호출 x
    @VisibleForTesting
    void setCreatedByForTest(Long createdBy) {
        this.createdBy = createdBy;
    }
}
