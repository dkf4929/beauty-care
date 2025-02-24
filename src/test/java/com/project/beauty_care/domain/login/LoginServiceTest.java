package com.project.beauty_care.domain.login;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.login.dto.LoginRequestDto;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.MemberRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.exception.RequestInvalidException;
import com.project.beauty_care.global.security.dto.AppUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class LoginServiceTest extends IntegrationTestSupport {
    @Autowired
    protected LoginService loginService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("유효한 아이디와 패스워드를 사용하여 로그인이 성공한다")
    @Test
    void login() {
        //given
        Member member = createMember("admin", Role.ADMIN, "qwer1234", "admin");

        LoginRequestDto request = LoginRequestDto.builder()
                .loginId("admin")
                .password("qwer1234")
                .build();

        // when
        AppUser loginMember = loginService.login(request);

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

        LoginRequestDto request = LoginRequestDto.builder()
                .loginId(anonymousLoginId)
                .password("1234")
                .build();

        // when, then
        assertThatThrownBy(() -> loginService.login(request))
                .isInstanceOf(RequestInvalidException.class)
                .extracting("errors.message")
                .isEqualTo(Errors.ANONYMOUS_USER.getMessage());
    }

    @DisplayName("잘못된 비밀번호로 로그인을 시도할 경우 예외가 발생한다.")
    @Test
    void loginWithInvalidPassword() {
        // given
        Member member = createMember("admin", Role.ADMIN, "qwer1234", "admin");

        LoginRequestDto request = LoginRequestDto.builder()
                .loginId("admin")
                .password("1234")
                .build();

        // when, then
        assertThatThrownBy(() -> loginService.login(request))
                .isInstanceOf(RequestInvalidException.class)
                .extracting("errors.message")
                .isEqualTo(Errors.PASSWORD_MISS_MATCH.getMessage());
    }

    private Member createMember(String loginId, Role role, String password, String name) {
        Member member = Member.builder()
                .loginId(loginId)
                .role(role)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();

        return memberRepository.save(member);
    }
}