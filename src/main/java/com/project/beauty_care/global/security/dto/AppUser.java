package com.project.beauty_care.global.security.dto;

import com.project.beauty_care.domain.menu.dto.UserMenuResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AppUser implements UserDetails {
    private Long memberId;
    private String loginId;
    private String name;
    private RoleResponse role;
    private UserMenuResponse authorityMenu;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.getRoleName()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Builder
    public AppUser(Long memberId, String loginId, String name, RoleResponse role, UserMenuResponse authorityMenu) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.name = name;
        this.role = role;
        this.authorityMenu = authorityMenu;
    }
}
