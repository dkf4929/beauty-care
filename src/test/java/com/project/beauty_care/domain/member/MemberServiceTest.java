package com.project.beauty_care.domain.member;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.member.dto.MemberCreateRequest;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.UniqueConstraint;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import org.assertj.core.groups.Tuple;
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

    private MemberCreateRequest createMemberRequest() {
        return new MemberCreateRequest("user", "qwer1234", "user");
    }
}