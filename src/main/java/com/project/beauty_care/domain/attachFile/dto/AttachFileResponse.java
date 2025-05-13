package com.project.beauty_care.domain.attachFile.dto;

import com.project.beauty_care.domain.dto.BaseTimeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class AttachFileResponse extends BaseTimeDto {
    @Schema(description = "파일 ID", example = "1")
    private Long fileId;

    @Schema(description = "파일 경로", example = "./UPLOAD/FILE/BOARD/1/0232b309-7b60-402e-8976-9aebb58f0d98.png")
    private String fileFullPath;

    @Schema(description = "원본 파일명", example = "사진1.png")
    private String originalFileName;

    @Schema(description = "파일 크기", example = "14169")
    private Long fileSize;
}
