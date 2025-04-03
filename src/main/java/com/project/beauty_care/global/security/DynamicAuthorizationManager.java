package com.project.beauty_care.global.security;

import com.project.beauty_care.global.security.dto.AppUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        String requestUri = request.getRequestURI();
        Authentication authenticationObject = authentication.get();

        // 권한 있는지 check
        if (!(authenticationObject.getPrincipal() instanceof AppUser principal)) {
            return new AuthorizationDecision(false);
        }

        boolean isPermit = principal.getRole().getUrlPatterns().stream()
                .anyMatch(urlPattern -> pathMatcher.match(urlPattern, requestUri));

        return new AuthorizationDecision(isPermit);
    }
}
