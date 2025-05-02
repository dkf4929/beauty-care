package com.project.beauty_care.domain.attachFile;

import com.project.beauty_care.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttachFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MappedEntity mappedEntity;

    @NotBlank
    private String mappedId;

    @NotBlank
    private String fileName;

    @NotBlank
    private String filePath;

    @NotBlank
    @Column(unique = true)
    private String storedFileName;

    @NotBlank
    private String extension;

    @NotNull
    private Long fileSize;

    @Builder
    public AttachFile(MappedEntity mappedEntity,
                      String mappedId,
                      String fileName,
                      String storedFileName,
                      String filePath,
                      String extension,
                      Long fileSize) {
        this.mappedEntity = mappedEntity;
        this.mappedId = mappedId;
        this.fileName = fileName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
        this.extension = extension;
        this.fileSize = fileSize;
    }
}
