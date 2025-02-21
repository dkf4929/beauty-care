package com.project.beauty_care.domain.member;

import com.project.beauty_care.domain.BaseEntity;
import com.project.beauty_care.global.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
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

    private String role;

    private LocalDateTime lastLoginDateTime;

    public void updateLastLoginDateTime(LocalDateTime lastLoginDateTime) {
        this.lastLoginDateTime = lastLoginDateTime;
    }

    @Builder
    public Member(Long id, String loginId, String password, String name, Role role, LocalDateTime lastLoginDateTime) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role.getValue();
        this.lastLoginDateTime = lastLoginDateTime;
    }
}
