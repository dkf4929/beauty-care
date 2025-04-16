package com.project.beauty_care.domain.menu;

import com.project.beauty_care.domain.BaseEntity;
import com.project.beauty_care.domain.menu.dto.AdminMenuUpdateRequest;
import com.project.beauty_care.domain.menuRole.MenuRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UQ_MENU_NAME", columnNames = {"menu_name"}),
        @UniqueConstraint(name = "UQ_MENU_PATH", columnNames = {"menu_path"})
})
public class Menu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String menuName;

    @NotBlank
    @Column(nullable = false)
    private String menuPath;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @ColumnDefault("0")
    private Integer sortNumber;

    @NotNull
    private Boolean isLeaf;

    @NotNull
    private Boolean isUse;

    @ManyToOne
    @JoinColumn(name = "upper_id")
    private Menu parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("sortNumber ASC")
    private List<Menu> children = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuRole> menuRole = new ArrayList<>();

    public Menu updateMenu(AdminMenuUpdateRequest request, List<MenuRole> menuRole) {
        this.menuName = request.getMenuName();
        this.menuPath = request.getMenuPath();
        this.description = request.getDescription();
        this.sortNumber = request.getSortNumber();
        this.isLeaf = request.getIsLeaf();
        this.isUse = request.getIsUse();
        this.menuRole = menuRole;

        return this;
    }

    @Builder
    public Menu(String menuName, String menuPath, String description, Integer sortNumber, Boolean isLeaf, Boolean isUse, Menu parent) {
        this.menuName = menuName;
        this.menuPath = menuPath;
        this.description = description;
        this.sortNumber = sortNumber;
        this.isLeaf = isLeaf;
        this.isUse = isUse;
        this.parent = parent;
    }
}
