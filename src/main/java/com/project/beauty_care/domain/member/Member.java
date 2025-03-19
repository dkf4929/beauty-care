package com.project.beauty_care.domain.member;

import com.project.beauty_care.domain.BaseEntity;
import com.project.beauty_care.domain.member.dto.AdminMemberCreateRequest;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
import com.project.beauty_care.global.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(name = "UQ_MEMBER_LOGIN_ID", columnNames = {"login_id"}))
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;

    @NotNull
    private String password;

    @NotNull
    private String name;

    private String role;

    private Boolean isUse;

    private LocalDateTime lastLoginDateTime;

    public void updateLastLoginDateTime(LocalDateTime lastLoginDateTime) {
        this.lastLoginDateTime = lastLoginDateTime;
    }

    @Builder
    public Member(String loginId, String password, String name, Role role, LocalDateTime lastLoginDateTime) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role.getValue();
        this.isUse = Boolean.TRUE;
        this.lastLoginDateTime = lastLoginDateTime;
    }

    public static Member createMember(PublicMemberCreateRequest request, String password) {
        return Member.builder()
                .loginId(request.getLoginId())
                .password(password)
                .name(request.getName())
                .role(Role.USER)
                .build();
    }

    public static Member createMember(AdminMemberCreateRequest request, String password) {
        return Member.builder()
                .loginId(request.getLoginId())
                .password(password)
                .name(request.getName())
                .role(request.getRole())
                .build();
    }

    public void deleteMember() {this.isUse = Boolean.FALSE;}
}
