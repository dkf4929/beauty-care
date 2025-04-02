package com.project.beauty_care.global.security;

import com.project.beauty_care.domain.role.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final RoleService roleService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        String requestUri = request.getRequestURI();

        // url 패턴과 일치하는 Role을 찾는다.
        List<String> roleList = roleService.findRoleNameByUrlPattern(requestUri);

        AuthorizationDecision authorizationDecision = new AuthorizationDecision(false);

        // 권한 있는지 check
        for (String roleName : roleList) {
            Authentication grantedAuthentication = authentication.get();

            // 권한 일치 -> pass
            if (grantedAuthentication.getAuthorities().contains(new SimpleGrantedAuthority(roleName)))
                authorizationDecision = new AuthorizationDecision(Boolean.TRUE);
        }

        return authorizationDecision;
    }
}
