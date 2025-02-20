package com.project.beauty_care.global.security.jwt;

import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.PermitSvc;
import com.project.beauty_care.global.exception.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

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

    private boolean isPermitPath(String requestURI) {
        return Arrays.stream(PermitSvc.toArrayPath())
                .anyMatch(permitPath -> pathMatcher.match(permitPath, requestURI));
    }
}
