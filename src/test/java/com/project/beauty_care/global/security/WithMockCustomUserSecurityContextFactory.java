package com.project.beauty_care.global.security;

import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.global.security.dto.AppUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {
        // AppUser 객체 생성
        Role role = Role.builder()
                .roleName("ADMIN")
                .build();

        AppUser appUser = AppUser.builder()
                .memberId(withMockCustomUser.memberId())
                .loginId(withMockCustomUser.loginId())
                .name(withMockCustomUser.name())
                .role(RoleMapper.INSTANCE.toSimpleDto(role))
                .build();

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                appUser,
                "",
                List.of(new SimpleGrantedAuthority(role.getRoleName())));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        return context;
    }
}