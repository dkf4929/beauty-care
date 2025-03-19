package com.project.beauty_care.domain.member;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.member.dto.AdminMemberCreateRequest;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${initial.password}")
    private String initialPassword;

    @DisplayName("사용자 정보를 입력받아, 회원 가입한다.")
    @Test
    void createMemberForPublic() {
        // given
        PublicMemberCreateRequest request = buildCreatePublicMemberRequest();
        String password = request.getPassword();

        // when
        Member savedMember = service.createMemberPublic(request);

        // then
        assertThat(savedMember)
                .extracting("loginId", "name")
                .containsExactly(savedMember.getLoginId(), savedMember.getName());

        assertTrue(passwordEncoder.matches(password, savedMember.getPassword()));
    }

    @DisplayName("동일한 로그인 아이디로 회원가입 시도하면, 예외 발생")
    @Test
    void createMemberPublicWithIdenticalLoginId() {
        // given
        PublicMemberCreateRequest request = buildCreatePublicMemberRequest();
        PublicMemberCreateRequest duplicatedRequest = buildCreatePublicMemberRequest();

        service.createMemberPublic(request);

        // when, then
        // 메시지에 제약조건을 포함하는지 확인
        assertThatThrownBy(() -> service.createMemberPublic(duplicatedRequest))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining(UniqueConstraint.UQ_MEMBER_LOGIN_ID.name());
    }

    @DisplayName("사용자 삭제(softDelete)")
    @Test
    void softDeleteMember() {
        // given
        PublicMemberCreateRequest request = buildCreatePublicMemberRequest();
        Member member = service.createMemberPublic(request);

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

    @DisplayName("사용자 정보를 입력받아, 사용자를 생성한다.(관리자)")
    @Test
    void createMemberForAdmin() {
        // given, when
        Member savedMember = saveMember();

        // then
        assertThat(savedMember)
                .extracting("loginId", "name", "role", "isUse")
                .containsExactly(savedMember.getLoginId(), savedMember.getName(), savedMember.getRole(), savedMember.getIsUse());
    }

    @DisplayName("회원 생성 시, 초기 비밀번호 확인")
    @Test
    void initialPasswordCheckWhenMemberCreated() {
        // given, when
        Member savedMember = saveMember();

        // then
        assertTrue(passwordEncoder.matches(initialPassword, savedMember.getPassword()));
    }

    @DisplayName("동일한 로그인 아이디로 회원가입 시도하면, 예외 발생")
    @Test
    void createMemberAdminWithIdenticalLoginId() {
        // given
        AdminMemberCreateRequest request = buildCreateAdminMemberRequest();
        AdminMemberCreateRequest duplicatedRequest = buildCreateAdminMemberRequest();

        service.createMemberAdmin(request);

        // when, then
        // 메시지에 제약조건을 포함하는지 확인
        assertThatThrownBy(() -> service.createMemberAdmin(duplicatedRequest))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining(UniqueConstraint.UQ_MEMBER_LOGIN_ID.name());
    }

    private PublicMemberCreateRequest buildCreatePublicMemberRequest() {
        return PublicMemberCreateRequest.builder()
                .loginId("user")
                .password("qwer1234")
                .name("user")
                .build();
    }

    private Member saveMember() {
        AdminMemberCreateRequest request = buildCreateAdminMemberRequest();
        return service.createMemberAdmin(request);
    }

    private AdminMemberCreateRequest buildCreateAdminMemberRequest() {
        return AdminMemberCreateRequest.builder()
                .loginId("user")
                .isUse(Boolean.TRUE)
                .role(Role.USER)
                .name("user")
                .build();
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