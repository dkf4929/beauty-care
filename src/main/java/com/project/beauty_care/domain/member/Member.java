package com.project.beauty_care.domain.member;

import com.project.beauty_care.domain.BaseEntity;
import com.project.beauty_care.domain.member.dto.AdminMemberUpdateRequest;
import com.project.beauty_care.domain.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private Boolean isUse;

    private LocalDate deletedDate;

    private LocalDateTime lastLoginDateTime;

    // FOR ADMIN
    public void updateMember(AdminMemberUpdateRequest request, Role role) {
        this.isUse = request.getIsUse();
        this.role = role;
    }

    // FOR USER
    public void updateMember(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public void updateLastLoginDateTime(LocalDateTime lastLoginDateTime) {
        this.lastLoginDateTime = lastLoginDateTime;
    }

    @Builder
    public Member(String loginId,
                  String password,
                  String name,
                  Role role,
                  LocalDateTime lastLoginDateTime,
                  LocalDate deletedDate,
                  Boolean isUse) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role;
        this.lastLoginDateTime = lastLoginDateTime;
        this.deletedDate = deletedDate;
        this.isUse = isUse;
    }

    public static Member createForTest(Long id, String loginId, String password, String name, Role role) {
        Member member = new Member();
        member.id = id;
        member.loginId = loginId;
        member.password = password;
        member.name = name;
        member.role = role;

        return member;
    }

    public void softDelete(LocalDate deletedDate) {
        this.deletedDate = deletedDate;
        this.isUse = Boolean.FALSE;
    }

    public void deleteCancel() {
        this.isUse = Boolean.TRUE;
        this.deletedDate = null;
    }
}
