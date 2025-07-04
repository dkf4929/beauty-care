package com.project.beauty_care.domain.board.dto;

import com.project.beauty_care.domain.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BoardCriteria {
    @Schema(description = "게시물 제목", example = "제목")
    private String title;

    @Schema(description = "게시물 내용", example = "안녕하세요")
    private String content;

    @Schema(description = "작성자 ID", example = "1")
    private Long createdBy;

    @NotNull
    @Schema(description = "게시물 타입", example = "FREE")
    private BoardType boardType;

    @Schema(description = "게시물 등급", example = "user:board:grade:normal")
    private String grade;
}
