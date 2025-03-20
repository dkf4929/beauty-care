package com.project.beauty_care.domain.member.service;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.AdminMemberCreateRequest;
import com.project.beauty_care.domain.member.dto.AdminMemberUpdateRequest;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.enums.Role;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.security.dto.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class MemberServiceTest extends IntegrationTestSupport {
    @Autowired
    private MemberService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private MemberRepository repository;

    @Value("${initial.password}")
    private String initialPassword;

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
        // when
        when(repository.findAll()).thenReturn(List.of(member1, member2, member3));

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
        final Long id = 1L;
        final String loginId = "test";
        final String password = "1234";
        final String name = "test";
        final Role role = Role.USER;

        when(repository.findById(any()))
                .thenReturn(Optional.of(
                        Member.createForTest(id, loginId, password, name, role)
                ));

        // when
        MemberResponse findMember = service.findMemberById(id);

        // then
        assertThat(findMember)
                .extracting("loginId", "name", "role")
                .containsExactly(loginId, name, role.getValue());
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

    @DisplayName("회원 수정 시나리오 FOR ADMIN")
    @TestFactory
    Collection<DynamicTest> updateMemberForAdmin() {
        return List.of(
                DynamicTest.dynamicTest("정상 시나리오", () -> {
                    // given
                    final Long id = 1L;
                    final Role role = Role.USER;
                    final boolean isUse = true;

                    final Long loginMemberId = 2L;

                    AdminMemberUpdateRequest request = buildAdminMemberUpdateRequest(id, role, isUse);

                    when(repository.findById(anyLong()))
                            .thenReturn(
                                    Optional.of(
                                            Member.createForTest(id, "test", "1234", "test", Role.USER)
                                    )
                            );

                    AppUser appUser = buildAppUser(loginMemberId, "test", Role.ADMIN, "test");

                    // when
                    MemberResponse memberResponse = service.updateMemberAdmin(request, appUser);

                    // then
                    assertThat(memberResponse)
                            .isNotNull()
                            .extracting("id", "role", "isUse")
                            .containsExactly(id, role.getValue(), isUse);
                }),
                DynamicTest.dynamicTest("관리자 권한의 회원 수정 시도 시, 예외 발생", () -> {
                    // given
                    final String exMessage = "관리자 권한을 가진 사용자는 수정할 수 없습니다.";

                    when(repository.findById(anyLong()))
                            .thenReturn(Optional.of(
                                    Member.createForTest(1L, "test", "1234", "test", Role.ADMIN)
                            ));

                    AdminMemberUpdateRequest request = buildAdminMemberUpdateRequest(1L, Role.ADMIN, Boolean.FALSE);
                    AppUser appUser = buildAppUser(2L, "test", Role.ADMIN, "test");

                    // when, then
                    assertThatThrownBy(() -> service.updateMemberAdmin(request, appUser))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(exMessage);
                }),
                DynamicTest.dynamicTest("로그인 사용자의 정보를 수정하려고 시도하면, 예외 발생", () -> {
                    // given
                    final String exMessage = "개인정보 수정은 사용자 기능입니다.";

                    when(repository.findById(anyLong()))
                            .thenReturn(Optional.of(
                                    Member.createForTest(1L, "test", "1234", "test", Role.ADMIN)
                            ));

                    AdminMemberUpdateRequest request = buildAdminMemberUpdateRequest(1L, Role.ADMIN, Boolean.FALSE);
                    AppUser appUser = buildAppUser(1L, "test", Role.ADMIN, "test");

                    // when, then
                    assertThatThrownBy(() -> service.updateMemberAdmin(request, appUser))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(exMessage);
                })
        );
    }

    private PublicMemberCreateRequest buildPublicMemberCreateRequest() {
        return PublicMemberCreateRequest.builder()
                .loginId("user")
                .password("qwer1234")
                .name("user")
                .build();
    }

    private AdminMemberCreateRequest buildAdminMemberCreateRequest() {
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

    private AdminMemberUpdateRequest buildAdminMemberUpdateRequest(Long id, Role role, Boolean isUse) {
        return AdminMemberUpdateRequest.builder()
                .id(id)
                .role(role)
                .isUse(isUse)
                .build();
    }

    private AppUser buildAppUser(Long memberId, String loginId, Role role, String name) {
        return AppUser.builder()
                .memberId(memberId)
                .loginId(loginId)
                .role(role.getValue())
                .name(name)
                .build();
    }
}