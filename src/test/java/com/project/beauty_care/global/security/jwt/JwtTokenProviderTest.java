package com.project.beauty_care.global.security.jwt;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.exception.NoAuthorityMember;
import com.project.beauty_care.global.exception.TokenExpiredException;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.security.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest extends IntegrationTestSupport {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${token.access}")
    private long accessTokenValidTime;

    @DisplayName("인증 객체와 현재 시간을 받아, 토큰을 생성한다.")
    @Test
    void generateTokenWithAuthentication() {
        // given, when
        long now = new Date().getTime();
        LoginResponse loginResponse =
                generateToken(now, Role.USER.getValue());

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
        // then
        assertThatThrownBy(() -> generateToken(new Date().getTime(), role))
                .isInstanceOf(NoAuthorityMember.class)
                .hasFieldOrPropertyWithValue("errors.message", Errors.NO_AUTHORITY_MEMBER.getMessage())
                .hasFieldOrPropertyWithValue("errors.errorCode", Errors.NO_AUTHORITY_MEMBER.getErrorCode());
    }

    @DisplayName("토큰의 권한 정보가 일치하는지 확인")
    @Test
    void getAuthentication() {
        // given
        LoginResponse loginResponse = generateToken(new Date().getTime(), Role.USER.getValue());

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(loginResponse.getAccessToken());

        // then
        assertThat(authentication).isNotNull()
                .extracting("principal")
                .extracting("role", "name", "loginId", "memberId")
                .containsExactly(Role.USER.getValue(), "user", "user", 1L);
    }

    @DisplayName("토큰 만료시간 체크 시나리오")
    @TestFactory
    Collection<DynamicTest> validateTokenDynamicTest() {
        return List.of(
                DynamicTest.dynamicTest("토큰 만료 시 예외가 발생한다(과거 시점)", () -> {
                    // given : expired -> 만료(과거일)
                    final long expiredPast = LocalDate.of(1900, 1, 1)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli();

                    LoginResponse loginResponse = generateToken(expiredPast, Role.USER.getValue());

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

                    LoginResponse loginResponse = generateToken(expired1SecBefore, Role.USER.getValue());

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

                    LoginResponse loginResponse = generateToken(expiredEquals, Role.USER.getValue());

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

                    LoginResponse loginResponse = generateToken(validTime, Role.USER.getValue());

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

                    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
                    Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenString);

                    // when
                    String token = jwtTokenProvider.resolveToken(request);

                    // then
                    assertThat(token).isEqualTo(tokenString);
                }),
                DynamicTest.dynamicTest("인증 헤더에 토큰이 없으면 null을 리턴한다.", () -> {
                    // given
                    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
                    Mockito.when(request.getHeader("Authorization")).thenReturn(null);

                    // when
                    String token = jwtTokenProvider.resolveToken(request);

                    // then
                    assertThat(token).isNull();
                })
        );
    }

    private LoginResponse generateToken(long now, String role) {
        // principal
        AppUser user = AppUser.builder()
                .role(role)
                .name("user")
                .loginId("user")
                .memberId(1L)
                .build();

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(user, "", List.of(new SimpleGrantedAuthority(role)));

        return jwtTokenProvider.generateToken(authentication, now);
    }
}