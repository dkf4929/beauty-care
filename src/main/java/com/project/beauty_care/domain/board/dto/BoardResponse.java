package com.project.beauty_care.domain.board.dto;

import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.dto.BaseDto;
import com.project.beauty_care.domain.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class BoardResponse extends BaseDto {
    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "게시글 타입", example = "BOARD")
    private BoardType boardType;

    @Schema(description = "게시글 등급", example = "일반")
    private CodeResponse grade;

    @Schema(description = "제목", example = "게시물1")
    private String title;

    @Schema(description = "내용", example = "안녕하세요.")
    private String content;

    @Schema(description = "파일 경로", example = "./UPLOAD/BOARD/1/0232b309-7b60-402e-8976-9aebb58f0d98.png")
    private List<String> attachFiles = new ArrayList<>();

    @Schema(description = "사용 여부", example = "true")
    private Boolean isUse;

    @Schema(description = "조회 수", example = "0")
    private Integer readCount;
}
