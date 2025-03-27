package com.project.beauty_care.domain.member.repository;

import com.project.beauty_care.RepositoryTestSupport;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.UniqueConstraint;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberRepositoryTest extends RepositoryTestSupport {
    @Autowired
    private MemberRepository repository;

    final Role role = buildRole(Authentication.USER.getName());

    @DisplayName("로그인 아이디와 일치하는 멤버를 조회한다.")
    @Test
    void findByLoginIdAndIsUseIsTrue() {
        //given
        createMember("user1", null, "user1");

        //when
        Member findMember = repository.findByLoginIdAndIsUseIsTrue("user1")
                .orElseThrow(() -> new RequestInvalidException(Errors.ANONYMOUS_USER));

        assertThat(findMember)
                .extracting("name", "loginId")
                .containsExactly("user1", "user1");
    }

    @DisplayName("동일한 로그인 아이디로 회원 생성 => 예외 발생")
    @Test
    void createMemberWithIdenticalLoginId() {
        createMember("user1", role, "user1");

        // when, then
        // 메시지에 제약조건을 포함하는지 확인
        assertThatThrownBy(() -> createMember("user1", role, "user1"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining(UniqueConstraint.UQ_MEMBER_LOGIN_ID.name());
    }

    @DisplayName("사용자를 생성한다.")
    @Test
    void createMemberTest() {
        // given
        final String name = "user1";
        final String loginId = "user1";

        // when, then
        assertThat(createMember(name, role, loginId))
                .extracting("loginId", "name", "role", "isUse")
                .containsExactly(loginId, name, role, Boolean.TRUE);
    }

    private Member createMember(String name,
                                Role role,
                                String loginId) {
        Member member = Member.builder()
                .name(name)
                .role(role)
                .loginId(loginId)
                .password("1234")
                .build();

        return repository.save(member);
    }

    private Role buildRole(String role) {
        return Role.builder()
                .roleName(role)
                .urlPatterns(Collections.emptyMap())
                .build();
    }
}