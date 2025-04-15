package com.project.beauty_care.domain.menuRole;

import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.role.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder
    public MenuRole(Role role, Menu menu) {
        this.role = role;
        this.menu = menu;
    }
}
