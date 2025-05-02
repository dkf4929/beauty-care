package com.project.beauty_care.domain.attachFile.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class TempFileDto {
    private String tempFileFullPath;
    private String originalFileName;
    private String storedFileName;
    private Long size;
}
