package com.project.beauty_care.domain.boardReport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BoardReportCreateRequest {
    @NotNull
    private Long boardId;

    @NotBlank
    private String reason;
}
