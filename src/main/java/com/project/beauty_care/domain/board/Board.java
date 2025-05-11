package com.project.beauty_care.domain.board;

import com.project.beauty_care.domain.BaseEntity;
import com.project.beauty_care.domain.attachFile.AttachFile;
import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.enums.BoardType;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

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
}
