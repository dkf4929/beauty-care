package com.project.beauty_care.domain.member;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.member.dto.MemberCreateRequest;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.enums.UniqueConstraint;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class MemberServiceTest extends IntegrationTestSupport {
    @Autowired
    private MemberService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository repository;

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
    void createMemberWithIdenticalLoginId() {
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

    @DisplayName("사용자 삭제(softDelete)")
    @Test
    void softDeleteMember() {
        // given
        MemberCreateRequest request = createMemberRequest();
        Member member = service.createMember(request);

        // isUse => true
        Boolean isUse = member.getIsUse();

        // when
        // isUse => false
        service.softDeleteMember(member.getId());

        // then
        assertThat(repository.findByIdAndIsUseIsTrue(member.getId())).isEmpty();
        assertThat(isUse).isTrue();
        assertThat(member.getIsUse()).isFalse();
    }

    @DisplayName("존재하지 않는 사용자를 삭제하려고 시도하면, 예외 발생")
    @Test
    void softDeleteMemberWithNotPresentMember() {
        // given
        Long deleteMemberId = 9999L;

        // when, then
        assertThatThrownBy(() -> service.softDeleteMember(deleteMemberId))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("errors.message", "errors.errorCode")
                .containsExactly(Errors.NOT_FOUND_MEMBER.getMessage(), Errors.NOT_FOUND_MEMBER.getErrorCode());
    }

    @DisplayName("사용자 조회(ALL)")
    @Test
    void findAllMembers() {
        // given
        Member member1 = createMember("test1");
        Member member2 = createMember("test2");
        Member member3 = createMember("test3");

        repository.saveAll(List.of(member1, member2, member3));
        // when
        List<MemberResponse> members = service.findAllMembers();

        // then
        assertThat(members)
                .hasSize(3)
                .extracting("loginId", "name", "role")
                .containsExactly(
                        tuple("test1", "test", Role.USER.getValue()),
                        tuple("test2", "test", Role.USER.getValue()),
                        tuple("test3", "test", Role.USER.getValue())
                );
    }

    @DisplayName("특정 사용자 조회")
    @Test
    void findMemberById() {
        // given
        Member member = createMember("test");
        repository.save(member);

        // when
        MemberResponse findMember = service.findMemberById(member.getId());

        // then
        assertThat(findMember)
                .extracting("loginId", "name", "role")
                .containsExactly(member.getLoginId(), member.getName(), member.getRole());
    }

    @DisplayName("존재하지 않는 사용자 조회 시, 예외 발생")
    @Test
    void findMemberByNotPresentId() {
        // given, when, then
        assertThatThrownBy(() -> service.findMemberById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("errors.message", "errors.errorCode")
                .containsExactly(Errors.NOT_FOUND_MEMBER.getMessage(), Errors.NOT_FOUND_MEMBER.getErrorCode());
    }

    private MemberCreateRequest createMemberRequest() {
        return new MemberCreateRequest("user", "qwer1234", "user");
    }

    private Member createMember(String loginId) {
        return Member.builder()
                .name("test")
                .loginId(loginId)
                .role(Role.USER)
                .password("qwer1234")
                .build();
    }
}