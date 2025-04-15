package com.project.beauty_care.domain.member.service;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.AdminMemberUpdateRequest;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
import com.project.beauty_care.domain.member.dto.UserMemberUpdateRequest;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.PasswordMissMatchException;
import com.project.beauty_care.global.security.dto.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private MemberService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private MemberRepository repository;

    @MockitoBean
    private RoleRepository roleRepository;

    @Value("${initial.password}")
    private String initialPassword;

    final Role USER = buildRole(Authentication.USER.getName());
    final Role ADMIN = buildRole(Authentication.ADMIN.getName());

    @DisplayName("존재하지 않는 사용자를 삭제하려고 시도하면, 예외 발생")
    @Test
    void softDeleteMemberWithNotPresentMember() {
        // given
        Long deleteMemberId = 9999L;

        // when, then
        assertThatThrownBy(() -> service.softDeleteMember(deleteMemberId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasFieldOrPropertyWithValue("errors.message", Errors.NOT_FOUND_MEMBER.getMessage())
                .hasFieldOrPropertyWithValue("errors.errorCode", Errors.NOT_FOUND_MEMBER.getErrorCode());
    }

    @DisplayName("사용자 조회(ALL)")
    @Test
    void findAllMembers() {
        // given
        Member member1 = buildMember("test1", USER);
        Member member2 = buildMember("test2", USER);
        Member member3 = buildMember("test3", USER);
        // when
        when(repository.findAll()).thenReturn(List.of(member1, member2, member3));

        List<MemberResponse> members = service.findAllMembers();

        // then
        assertThat(members)
                .hasSize(3)
                .extracting("loginId", "name", "role")
                .containsExactly(
                        tuple("test1", "test", USER.getRoleName()),
                        tuple("test2", "test", USER.getRoleName()),
                        tuple("test3", "test", USER.getRoleName())
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

        when(repository.findById(any()))
                .thenReturn(Optional.of(
                        Member.createForTest(id, loginId, password, name, USER)
                ));

        // when
        MemberResponse findMember = service.findMemberById(id);

        // then
        assertThat(findMember)
                .extracting("loginId", "name", "role")
                .containsExactly(loginId, name, USER.getRoleName());
    }

    @DisplayName("존재하지 않는 사용자 조회 시, 예외 발생")
    @Test
    void findMemberByNotPresentId() {
        // given, when, then
        assertThatThrownBy(() -> service.findMemberById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasFieldOrPropertyWithValue("errors.message", Errors.NOT_FOUND_MEMBER.getMessage())
                .hasFieldOrPropertyWithValue("errors.errorCode", Errors.NOT_FOUND_MEMBER.getErrorCode());
    }

    @DisplayName("회원 저장 시나리오 FOR PUBLIC")
    @TestFactory
    Collection<DynamicTest> createMemberForPublic() {
        return List.of(
                DynamicTest.dynamicTest("회원 저장(정상 case)", () -> {
                    // given
                    final String loginId = "test";
                    final String password = "1234";
                    final String confirmPassword = "1234";
                    final String name = "test";

                    PublicMemberCreateRequest request =
                            buildPublicMemberCreateRequest(loginId, name, password, confirmPassword);

                    when(repository.save(any()))
                            .thenReturn(Member.createForTest(1L, loginId, password, name, USER));

                    when(roleRepository.findById(any()))
                            .thenReturn(Optional.ofNullable(USER));

                    // when
                    Member savedMember = service.createMemberPublic(request);

                    // then
                    assertThat(savedMember)
                            .extracting("loginId", "name", "password")
                            .containsExactly(loginId, name, password);

                    // 한번 호출 됐는지 검증
                    verify(repository, times(1)).save(any());
                }),
                DynamicTest.dynamicTest("비밀번호와 비밀번호 확인 불일치 => 예외", () -> {
                    // given
                    PublicMemberCreateRequest request =
                            buildPublicMemberCreateRequest("test", "test", "1234", "12345");

                    // when, then
                    assertThatThrownBy(() -> service.createMemberPublic(request))
                            .isInstanceOf(PasswordMissMatchException.class)
                            .hasFieldOrPropertyWithValue("errors.message", Errors.PASSWORD_MISS_MATCH.getMessage())
                            .hasFieldOrPropertyWithValue("errors.errorCode", Errors.PASSWORD_MISS_MATCH.getErrorCode());
                })
        );
    }

    @DisplayName("회원 수정 시나리오 FOR USER")
    @TestFactory
    Collection<DynamicTest> updateMemberForUser() {
        return List.of(
                DynamicTest.dynamicTest("정상 시나리오", () -> {
                    // given
                    final String name = "test";
                    final String password = "1234";
                    final String confirmPassword = "1234";
                    final Long id = 1L;

                    UserMemberUpdateRequest request = buildUserMemberUpdateRequest(id, name, password, confirmPassword);

                    when(repository.findById(anyLong()))
                            .thenReturn(
                                    Optional.of(
                                            Member.createForTest(id, "test", "1234", "test", USER)
                                    )
                            );

                    // when
                    MemberResponse memberResponse = service.updateMemberUser(request);

                    // then
                    assertThat(memberResponse)
                            .isNotNull()
                            .extracting("id", "name")
                            .containsExactly(id, name);
                }),
                DynamicTest.dynamicTest("비밀번호 불일치 => 예외 발생", () -> {
                    // given
                    UserMemberUpdateRequest request = buildUserMemberUpdateRequest(1L, "test", "1234", "12345");

                    // when, then
                    assertThatThrownBy(() -> service.updateMemberUser(request))
                            .isInstanceOf(PasswordMissMatchException.class)
                            .hasFieldOrPropertyWithValue("errors.message", Errors.PASSWORD_MISS_MATCH.getMessage())
                            .hasFieldOrPropertyWithValue("errors.errorCode", Errors.PASSWORD_MISS_MATCH.getErrorCode());
                })
        );
    }

    @DisplayName("회원 수정 시나리오 FOR ADMIN")
    @TestFactory
    Collection<DynamicTest> updateMemberForAdmin() {
        return List.of(
                DynamicTest.dynamicTest("정상 시나리오", () -> {
                    // given
                    final Long id = 1L;
                    final boolean isUse = true;

                    final Long loginMemberId = 2L;

                    AdminMemberUpdateRequest request = buildAdminMemberUpdateRequest(id, Authentication.USER.getName(), isUse);

                    when(repository.findById(anyLong()))
                            .thenReturn(
                                    Optional.of(
                                            Member.createForTest(id, "test", "1234", "test", USER)
                                    )
                            );

                    when(roleRepository.findById(any()))
                            .thenReturn(Optional.ofNullable(ADMIN));

                    AppUser appUser = buildAppUser(loginMemberId, "test", ADMIN, "test");

                    // when
                    MemberResponse memberResponse = service.updateMemberAdmin(request, appUser);

                    // then
                    assertThat(memberResponse)
                            .isNotNull()
                            .extracting("id", "role", "isUse")
                            .containsExactly(id, ADMIN.getRoleName(), isUse);
                }),
                DynamicTest.dynamicTest("관리자 권한의 회원 수정 시도 시, 예외 발생", () -> {
                    // given
                    final String exMessage = "관리자 권한을 가진 사용자는 수정할 수 없습니다.";

                    when(repository.findById(anyLong()))
                            .thenReturn(Optional.of(
                                    Member.createForTest(1L, "test", "1234", "test", ADMIN)
                            ));

                    AdminMemberUpdateRequest request = buildAdminMemberUpdateRequest(1L, Authentication.ADMIN.getName(), Boolean.FALSE);
                    AppUser appUser = buildAppUser(2L, "test", ADMIN, "test");

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
                                    Member.createForTest(1L, "test", "1234", "test", ADMIN)
                            ));

                    AdminMemberUpdateRequest request = buildAdminMemberUpdateRequest(1L, Authentication.ADMIN.getName(), Boolean.FALSE);
                    AppUser appUser = buildAppUser(1L, "test", ADMIN, "test");

                    // when, then
                    assertThatThrownBy(() -> service.updateMemberAdmin(request, appUser))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(exMessage);
                })
        );
    }

    private Member buildMember(String loginId, Role role) {
        return Member.builder()
                .name("test")
                .loginId(loginId)
                .role(role)
                .password("qwer1234")
                .build();
    }

    private AdminMemberUpdateRequest buildAdminMemberUpdateRequest(Long id, String role, Boolean isUse) {
        return AdminMemberUpdateRequest.builder()
                .id(id)
                .role(role)
                .isUse(isUse)
                .build();
    }

    private UserMemberUpdateRequest buildUserMemberUpdateRequest(Long id, String name, String password, String confirmPassword) {
        return UserMemberUpdateRequest.builder()
                .id(id)
                .name(name)
                .password(password)
                .confirmPassword(confirmPassword)
                .build();
    }

    private AppUser buildAppUser(Long memberId, String loginId, Role role, String name) {
        return AppUser.builder()
                .memberId(memberId)
                .loginId(loginId)
                .role(RoleMapper.INSTANCE.toSimpleResponse(role))
                .name(name)
                .build();
    }

    private PublicMemberCreateRequest buildPublicMemberCreateRequest(String loginId,
                                                                     String name,
                                                                     String password,
                                                                     String confirmPassword) {
        return PublicMemberCreateRequest.builder()
                .loginId(loginId)
                .name(name)
                .password(password)
                .confirmPassword(confirmPassword)
                .build();
    }

    private Role buildRole(String role) {
        return Role.builder()
                .roleName(role)
                .urlPatterns(Collections.emptyMap())
                .build();
    }
}