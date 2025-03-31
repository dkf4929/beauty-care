package com.project.beauty_care.domain.role;

import com.project.beauty_care.domain.BaseEntity;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.role.dto.RoleUpdateRequest;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {
    @Id
    private String roleName;

    @Type(JsonType.class)
    @Column(columnDefinition = "longtext")
    private Map<String, Object> urlPatterns = new HashMap<>();

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    private Boolean isUse;

    @Builder
    public Role(String roleName, Map<String, Object> urlPatterns, Boolean isUse, List<Member> members) {
        this.roleName = roleName;
        this.urlPatterns = urlPatterns;
        this.isUse = isUse;
        this.members = members;
    }

    public void updateRole(RoleUpdateRequest request, Map<String, Object> urlPatterns) {
        this.roleName = request.getAfterRoleName();
        this.urlPatterns = urlPatterns;
        this.isUse = request.getIsUse();
    }
}
