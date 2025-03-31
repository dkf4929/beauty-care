package com.project.beauty_care.global.security.jwt;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.domain.role.service.RoleService;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.NoAuthorityMember;
import com.project.beauty_care.global.exception.TokenExpiredException;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.security.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest extends IntegrationTestSupport {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${token.access}")
    private long accessTokenValidTime;

    final String USER = com.project.beauty_care.global.enums.Authentication.USER.getName();
    final String ADMIN = com.project.beauty_care.global.enums.Authentication.ADMIN.getName();

    @MockitoBean
    RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @BeforeEach
    void setUp() {
    }

    @DisplayName("인증 객체와 현재 시간을 받아, 토큰을 생성한다.")
    @Test
    void generateTokenWithAuthentication() {
        // given, when
        final Role role = buildRole(USER);

        when(roleRepository.findAllByRoleName(any()))
                .thenReturn(List.of(role));

        long now = new Date().getTime();
        LoginResponse loginResponse =
                generateToken(now, role);

        // then
        assertThat(loginResponse).isNotNull();

        // 토큰 유효시간 확인
        assertThat(loginResponse.getAccessTokenExpiresIn()).isEqualTo(now + accessTokenValidTime);

        // 토큰 존재여부
        assertThat(loginResponse.getAccessToken()).isNotEmpty();
    }

    @DisplayName("유효하지 않은 권한 -> 예외 발생")
    @ParameterizedTest
    @CsvSource({"ROLE_MANAGER", "ROLE_SYSTEM_ADMIN"})
    void generateTokenWithInvalidAuthority(String role) {
        // given
        when(roleRepository.findAllByRoleName(anyString()))
                .thenReturn(Collections.emptyList());

        // then
        assertThatThrownBy(() -> generateToken(new Date().getTime(), buildRole(role)))
                .isInstanceOf(NoAuthorityMember.class)
                .hasFieldOrPropertyWithValue("errors.message", Errors.NO_AUTHORITY_MEMBER.getMessage())
                .hasFieldOrPropertyWithValue("errors.errorCode", Errors.NO_AUTHORITY_MEMBER.getErrorCode());
    }

    @DisplayName("토큰의 권한 정보가 일치하는지 확인")
    @Test
    void getAuthentication() {
        // given
        final Role role = buildRole(USER);

        when(roleRepository.findAllByRoleName(any())).thenReturn(List.of(role));

        when(roleRepository.findById(any())).thenReturn(Optional.of(role));

        LoginResponse loginResponse = generateToken(new Date().getTime(), buildRole(USER));

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(loginResponse.getAccessToken());

        // then
        assertThat(authentication).isNotNull()
                .extracting("principal")
                .extracting("role.roleName", "name", "loginId", "memberId")
                .containsExactly(USER, "user", "user", 1L);
    }

    @DisplayName("토큰 만료시간 체크 시나리오")
    @TestFactory
    Collection<DynamicTest> validateTokenDynamicTest() {
        when(roleRepository.findAllByRoleName(anyString()))
                .thenReturn(List.of(buildRole(ADMIN)));

        return List.of(
                DynamicTest.dynamicTest("토큰 만료 시 예외가 발생한다(과거 시점)", () -> {
                    // given : expired -> 만료(과거일)
                    final long expiredPast = LocalDate.of(1900, 1, 1)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli();

                    LoginResponse loginResponse = generateToken(expiredPast, buildRole(USER));

                    // when, then
                    assertThatThrownBy(() -> jwtTokenProvider.validateToken(loginResponse.getAccessToken()))
                            .isInstanceOf(TokenExpiredException.class)
                            .hasFieldOrPropertyWithValue("errors.message", Errors.TOKEN_EXPIRED.getMessage())
                            .hasFieldOrPropertyWithValue("errors.errorCode", Errors.TOKEN_EXPIRED.getErrorCode());
                }),
                // 경계값 test 1
                DynamicTest.dynamicTest("토큰 만료 +1초 -> 예외 발생", () -> {
                    // given: expired -> 만료 1초 전
                    final long expired1SecBefore = System.currentTimeMillis() - accessTokenValidTime - 1000;

                    LoginResponse loginResponse = generateToken(expired1SecBefore, buildRole(USER));

                    // when, then
                    assertThatThrownBy(() -> jwtTokenProvider.validateToken(loginResponse.getAccessToken()))
                            .isInstanceOf(TokenExpiredException.class)
                            .hasFieldOrPropertyWithValue("errors.message", Errors.TOKEN_EXPIRED.getMessage())
                            .hasFieldOrPropertyWithValue("errors.errorCode", Errors.TOKEN_EXPIRED.getErrorCode());
                }),
                // 경계값 test 2
                DynamicTest.dynamicTest("토큰 만료 시점과 현재 시각 일치 시, 예외 발생", () -> {
                    // given: expired -> 만료시점 동일
                    final long expiredEquals = System.currentTimeMillis() - accessTokenValidTime;

                    LoginResponse loginResponse = generateToken(expiredEquals, buildRole(USER));

                    // when, then
                    assertThatThrownBy(() -> jwtTokenProvider.validateToken(loginResponse.getAccessToken()))
                            .isInstanceOf(TokenExpiredException.class)
                            .hasFieldOrPropertyWithValue("errors.message", Errors.TOKEN_EXPIRED.getMessage())
                            .hasFieldOrPropertyWithValue("errors.errorCode", Errors.TOKEN_EXPIRED.getErrorCode());
                }),
                // 정상 : 유효한 토큰 (현재 시간 이후(+1s)의 만료 시간)
                DynamicTest.dynamicTest("토큰 만료 -1초 -> 정상 case", () -> {
                    // given
                    long validTime = System.currentTimeMillis() - accessTokenValidTime + 1000;

                    LoginResponse loginResponse = generateToken(validTime, buildRole(USER));

                    // when
                    boolean isValid = jwtTokenProvider.validateToken(loginResponse.getAccessToken());

                    // then
                    assertThat(isValid).isTrue();
                })
        );
    }

    @DisplayName("http 헤더로 부터, 토큰을 가져온다.")
    @TestFactory
    Collection<DynamicTest> resolveToken() {
        return List.of(
                DynamicTest.dynamicTest("http 헤더에서 토큰을 가져온다.", () -> {
                    // given
                    final String tokenString = "eyJhbGciOiJIUzI1NiIsIn....";

                    HttpServletRequest request = mock(HttpServletRequest.class);
                    when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenString);

                    // when
                    String token = jwtTokenProvider.resolveToken(request);

                    // then
                    assertThat(token).isEqualTo(tokenString);
                }),
                DynamicTest.dynamicTest("인증 헤더에 토큰이 없으면 null을 리턴한다.", () -> {
                    // given
                    HttpServletRequest request = mock(HttpServletRequest.class);
                    when(request.getHeader("Authorization")).thenReturn(null);

                    // when
                    String token = jwtTokenProvider.resolveToken(request);

                    // then
                    assertThat(token).isNull();
                })
        );
    }

    private LoginResponse generateToken(long now, Role role) {
        // principal
        AppUser user = AppUser.builder()
                .role(role)
                .name("user")
                .loginId("user")
                .memberId(1L)
                .build();

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(user, "", List.of(new SimpleGrantedAuthority(role.getRoleName())));

        return jwtTokenProvider.generateToken(authentication, now);
    }

    private Role buildRole(String role) {
        return Role.builder()
                .roleName(role)
                .urlPatterns(Collections.emptyMap())
                .build();
    }
}