package com.project.beauty_care.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {
    @Column(updatable = false)
    private LocalDateTime createdDateTime;

    private LocalDateTime updatedDateTime;

    @PrePersist
    protected void onCreate() {
        this.createdDateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDateTime = LocalDateTime.now();
    }
}
