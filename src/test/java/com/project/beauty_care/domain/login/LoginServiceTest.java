package com.project.beauty_care.domain.login;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import com.project.beauty_care.global.login.dto.LoginRequest;
import com.project.beauty_care.global.login.service.LoginService;
import com.project.beauty_care.global.security.dto.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoginServiceTest extends IntegrationTestSupport {
    @Autowired
    private LoginService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private MemberRepository memberRepository;

    @DisplayName("유효한 아이디와 패스워드를 사용하여 로그인이 성공한다")
    @Test
    void login() {
        //given
        Member member = buildMember("admin", buildRole(Authentication.ADMIN.name()), "qwer1234", "admin");

        LoginRequest request = LoginRequest.builder()
                .loginId("admin")
                .password("qwer1234")
                .build();

        when(memberRepository.findByLoginIdAndIsUseIsTrue(any()))
                .thenReturn(Optional.ofNullable(member));

        // when
        AppUser loginMember = service.login(request);

        // then
        assertThat(loginMember)
                .extracting("loginId", "name", "role")
                .containsExactly(member.getLoginId(), member.getName(), member.getRole());
    }

    @DisplayName("등록되지 않은 사용자로 로그인을 시도할 경우, 예외가 발생한다.")
    @Test
    void loginWithAnonymousMember() {
        // given
        final String anonymousLoginId = "anonymousLoginId";

        LoginRequest request = LoginRequest.builder()
                .loginId(anonymousLoginId)
                .password("1234")
                .build();

        // when, then
        assertThatThrownBy(() -> service.login(request))
                .isInstanceOf(RequestInvalidException.class)
                .hasFieldOrPropertyWithValue("errors.message", Errors.ANONYMOUS_USER.getMessage())
                .hasFieldOrPropertyWithValue("errors.errorCode", Errors.ANONYMOUS_USER.getErrorCode());;
    }

    @DisplayName("잘못된 비밀번호로 로그인을 시도할 경우 예외가 발생한다.")
    @Test
    void loginWithInvalidPassword() {
        // given
        Member member = buildMember("admin", buildRole(Authentication.ADMIN.name()), "qwer1234", "admin");
        final String invalidPassword = "1234";

        LoginRequest request = LoginRequest.builder()
                .loginId(member.getLoginId())
                .password(invalidPassword)
                .build();

        when(memberRepository.findByLoginIdAndIsUseIsTrue(any()))
                .thenReturn(Optional.ofNullable(member));

        // when, then
        assertThatThrownBy(() -> service.login(request))
                .isInstanceOf(RequestInvalidException.class)
                .hasFieldOrPropertyWithValue("errors.message", Errors.PASSWORD_MISS_MATCH.getMessage())
                .hasFieldOrPropertyWithValue("errors.errorCode", Errors.PASSWORD_MISS_MATCH.getErrorCode());
    }

    private Member buildMember(String loginId, Role role, String password, String name) {
        return Member.builder()
                .loginId(loginId)
                .role(role)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();
    }

    private Role buildRole(String role) {
        return Role.builder()
                .roleName(role)
                .urlPatterns(Collections.emptyMap())
                .build();
    }
}