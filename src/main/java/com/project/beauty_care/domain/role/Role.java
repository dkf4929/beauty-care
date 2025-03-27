package com.project.beauty_care.domain.role;

import com.project.beauty_care.domain.member.Member;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
public class Role {
    @Id
    private String roleName;

    @Type(JsonType.class)
    @Column(columnDefinition = "longtext")
    private Map<String, Object> urlPatterns;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    @Builder
    public Role(String roleName, Map<String, Object> urlPatterns) {
        this.roleName = roleName;
        this.urlPatterns = urlPatterns;
    }
}
