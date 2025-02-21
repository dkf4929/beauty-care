package com.project.beauty_care.domain.member;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class MemberRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private MemberRepository repository;

    @DisplayName("로그인 아이디와 일치하는 멤버를 조회한다.")
    @Test
    void findByLoginId() {
        //given
        Member member = createMember("user1", Role.USER, "user1", "1234");

        repository.save(member);

        //when
        Member findMember = repository.findByLoginId("user1")
                .orElseThrow(() -> new RequestInvalidException(Errors.ANONYMOUS_USER));

        assertThat(findMember)
                .extracting("name", "role", "loginId")
                .contains(
                        tuple("user1", Role.USER.getValue(), "user1")
                );
    }

    private Member createMember(String name, Role role, String loginId, String password) {
        return Member.builder()
                .name(name)
                .role(role)
                .loginId(loginId)
                .password(password)
                .build();
    }
}