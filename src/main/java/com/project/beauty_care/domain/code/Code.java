package com.project.beauty_care.domain.code;

import com.project.beauty_care.domain.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SQLRestriction("is_use = true")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Code extends BaseTimeEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upper_id")
    private Code parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("sortNumber ASC")
    private List<Code> children = new ArrayList<>();

    @NotNull
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @ColumnDefault("0")
    private Integer sortNumber;

    @NotNull
    private Boolean isUse;

    @PrePersist
    private void prePersist(){
        if(isUse == null) isUse = true;
        if(sortNumber == null) sortNumber = 0;
    }

    @Builder
    public Code(String id, Code parent, List<Code> children, String name, String description, Integer sortNumber, Boolean isUse) {
        this.id = id;
        this.parent = parent;
        this.children = children;
        this.name = name;
        this.description = description;
        this.sortNumber = sortNumber;
        this.isUse = isUse;
    }
}
