package com.project.beauty_care.domain.board.dto;

import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import com.project.beauty_care.domain.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BoardCreateRequest {
    @NotNull
    @Schema(description = "게시글 타입", allowableValues = {"FREE", "FAQ", "DATA_ROOM", "NOTIFICATION"})
    private BoardType boardType;

    @NotBlank
    @Schema(description = "게시글 등급", example = "user:board:grade:normal")
    private String grade;

    @NotBlank
    @Schema(description = "제목", example = "게시물1")
    private String title;

    @NotBlank
    @Schema(description = "내용", example = "안녕하세요.")
    private String content;

    @Schema(description = "임시 파일 정보")
    private List<TempFileDto> attachFiles = new ArrayList<>();

    @Schema(description = "게시물 사용(숨김) 여부", example = "false")
    private Boolean isUse;
}
