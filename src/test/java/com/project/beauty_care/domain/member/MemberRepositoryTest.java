package com.project.beauty_care.domain.member;

import com.project.beauty_care.RepositoryTestSupport;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRepositoryTest extends RepositoryTestSupport {
    @Autowired
    private MemberRepository repository;

    @DisplayName("로그인 아이디와 일치하는 멤버를 조회한다.")
    @Test
    void findByLoginIdAndIsUseIsTrue() {
        //given
        createMember();

        //when
        Member findMember = repository.findByLoginIdAndIsUseIsTrue("user1")
                .orElseThrow(() -> new RequestInvalidException(Errors.ANONYMOUS_USER));

        assertThat(findMember)
                .extracting("name", "role", "loginId")
                .containsExactly("user1", Role.USER.getValue(), "user1");
    }

    private void createMember() {
        Member member = Member.builder()
                .name("user1")
                .role(Role.USER)
                .loginId("user1")
                .password("1234")
                .build();

        repository.save(member);
    }
}