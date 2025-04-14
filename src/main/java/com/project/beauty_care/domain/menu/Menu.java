package com.project.beauty_care.domain.menu;

import com.project.beauty_care.domain.menuRole.MenuRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String menuName;

    @NotBlank
    @Column(nullable = false)
    private String menuPath;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuRole> menuRole = new ArrayList<>();
}
