package com.project.beauty_care.global.config;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.security.dto.AppUser;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.shaded.com.google.common.collect.Maps;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SpringSecurityAuditorAwareTest extends TestSupportWithOutRedis {
    @Autowired
    private SpringSecurityAuditorAware springSecurityAuditorAware;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @DisplayName("get authentication test")
    @ParameterizedTest
    @MethodSource("authenticationProvider")
    void getCurrentAuditor(AppUser authentication, Role role, Long expectedMemberId) {
        // given
        setAuthentication(authentication, role);

        // when
        Long memberId = springSecurityAuditorAware.getCurrentAuditor().orElse(null);

        // then
        assertThat(memberId).isEqualTo(expectedMemberId);
    }

    private static void setAuthentication(AppUser authentication, Role role) {
        if (ObjectUtils.isNotEmpty(authentication) && ObjectUtils.isNotEmpty(role)) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            authentication,
                            null,
                            List.of(new SimpleGrantedAuthority(role.getRoleName()))
                    )
            );
        } else
            SecurityContextHolder.clearContext();
    }

    private static Stream<Arguments> authenticationProvider() {
        Role adminRole = Role.builder()
                .roleName(Authentication.ADMIN.getName())
                .urlPatterns(Maps.newHashMap())
                .isUse(Boolean.TRUE)
                .build();

        AppUser adminUser = AppUser.builder()
                .memberId(1L)
                .role(RoleMapper.INSTANCE.toSimpleResponse(adminRole))
                .name("admin")
                .loginId("admin")
                .build();

        return Stream.of(
                Arguments.of(adminUser, adminRole, 1L),
                Arguments.of(null, null, null)
        );
    }
}