package com.project.beauty_care.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {
    @Column(updatable = false, name = "created_date_time")
    @CreatedDate
    protected LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(name = "updated_date_time")
    protected LocalDateTime updatedDateTime;
}
