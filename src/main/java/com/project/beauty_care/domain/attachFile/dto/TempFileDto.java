package com.project.beauty_care.domain.attachFile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class TempFileDto {
    @Schema(description = "임시 파일 경로", example = "./UPLOAD/TEMP/FILE/0232b309-7b60-402e-8976-9aebb58f0d98.png")
    private String tempFileFullPath;

    @Schema(description = "원본 파일명", example = "사진1.png")
    private String originalFileName;

    @Schema(description = "서버 파일명", example = "0232b309-7b60-402e-8976-9aebb58f0d98.png")
    private String storedFileName;

    @Schema(description = "파일 크기", example = "14169")
    private Long size;
}
