package com.project.beauty_care.global.security.jwt;

import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.PermitSvc;
import com.project.beauty_care.global.exception.JwtException;
import com.project.beauty_care.global.security.dto.AppUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // all-permit-path
        if (isPermitPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // authorizationHeader -> 토큰 추출
        String jwt = tokenProvider.resolveToken(request);

        // 토큰 유효성 검사
        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            throw new JwtException(Errors.NOT_LOGIN_USER);
        }

        filterChain.doFilter(request, response);
    }

    private static void setAuthenticationToSecurityContextForTest() {
        AppUser authentication = AppUser.builder()
                .memberId(1L)
                .loginId("admin")
                .name("admin")
                .role(RoleResponse.builder().roleName("ADMIN").isUse(Boolean.TRUE).build())
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        authentication,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );
    }

    private boolean isPermitPath(String requestURI) {
        return Arrays.stream(PermitSvc.values())
                .anyMatch(permitSvc -> pathMatcher.match(permitSvc.getRegex(), requestURI) ||
                        permitSvc.getPath().equals(requestURI));
    }
}
