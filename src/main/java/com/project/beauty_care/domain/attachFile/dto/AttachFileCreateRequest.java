package com.project.beauty_care.domain.attachFile.dto;

import com.project.beauty_care.domain.attachFile.MappedEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AttachFileCreateRequest {
    @NotEmpty
    private List<TempFileDto> tempFileList = new ArrayList<>();

    @NotNull
    private MappedEntity mappedEntity;

    @NotBlank
    @Schema(description = "매핑 ID", example = "1")
    private String mappedId;
}
