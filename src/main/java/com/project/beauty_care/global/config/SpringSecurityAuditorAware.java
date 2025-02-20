package com.project.beauty_care.global.config;

import com.project.beauty_care.global.security.dto.AppUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        // 인증 객체
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인 x -> empty
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // 인증 객체인지 검증
        if (principal instanceof AppUser) {
            return Optional.of(((AppUser) principal).getMemberId());
        }

        return Optional.empty();
    }

}
