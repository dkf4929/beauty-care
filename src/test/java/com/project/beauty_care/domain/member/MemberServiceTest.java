package com.project.beauty_care.domain.member;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.member.dto.MemberCreateRequest;
import com.project.beauty_care.global.enums.UniqueConstraint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class MemberServiceTest extends IntegrationTestSupport {
    @Autowired
    private MemberService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("사용자 정보를 입력받아, 회원 가입한다.")
    @Test
    void createMember() {
        // given
        MemberCreateRequest request = createMemberRequest();
        String password = request.getPassword();

        // when
        Member savedMember = service.createMember(request);

        // then
        assertThat(savedMember)
                .extracting("loginId", "name")
                .containsExactly(savedMember.getLoginId(), savedMember.getName());

        assertTrue(passwordEncoder.matches(password, savedMember.getPassword()));
    }

    @DisplayName("동일한 로그인 아이디로 회원가입 시도하면, 예외 발생")
    @Test
    void createMemberWithIdenticalPassword() {
        // given
        MemberCreateRequest request = createMemberRequest();
        MemberCreateRequest duplicatedRequest = createMemberRequest();

        service.createMember(request);

        // when, then
        // 메시지에 제약조건을 포함하는지 확인
        assertThatThrownBy(() -> service.createMember(duplicatedRequest))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining(UniqueConstraint.UQ_MEMBER_LOGIN_ID.name());
    }

    private MemberCreateRequest createMemberRequest() {
        return MemberCreateRequest.builder()
                .loginId("user")
                .name("user")
                .password("12345abc")
                .build();
    }
}