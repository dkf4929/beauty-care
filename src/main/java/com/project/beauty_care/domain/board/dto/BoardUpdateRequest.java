package com.project.beauty_care.domain.board.dto;

import com.project.beauty_care.domain.attachFile.dto.TempFileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardUpdateRequest {
    @NotBlank
    @Schema(description = "제목", example = "게시물1")
    private String title;

    @NotBlank
    @Schema(description = "내용", example = "안녕하세요.")
    private String content;

    @Schema(description = "임시 파일 정보")
    private List<TempFileDto> attachFiles = new ArrayList<>();
}
