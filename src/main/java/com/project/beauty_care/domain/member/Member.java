package com.project.beauty_care.domain.member;

import com.project.beauty_care.domain.BaseEntity;
import com.project.beauty_care.global.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String loginId;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime lastLoginDateTime;

    public void updateLastLoginDateTime(LocalDateTime lastLoginDateTime) {
        this.lastLoginDateTime = lastLoginDateTime;
    }
}
