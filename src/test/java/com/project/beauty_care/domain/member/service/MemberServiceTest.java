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
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.security.dto.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
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
    private MemberRepository mockRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private Logger logger;

    @Value("${initial.password}")
    private String initialPassword;

    final Role USER = buildRole(Authentication.USER.getName());
    final Role ADMIN = buildRole(Authentication.ADMIN.getName());

    @DisplayName("존재하지 않는 사용자를 삭제하려고 시도하면, 예외 발생")
    @Test
    void softDeleteMemberWithNotPresentMember() {
        // given
        AppUser appUser = buildAppUser(9999L, "test", Role.builder().build(), "test");

        // when, then
        assertThatThrownBy(() -> service.softDeleteMember(appUser, LocalDate.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasFieldOrPropertyWithValue("errors.message", Errors.NOT_FOUND_MEMBER.getMessage())
                .hasFieldOrPropertyWithValue("errors.errorCode", Errors.NOT_FOUND_MEMBER.getErrorCode());
    }

    @DisplayName("회원 탈퇴")
    @Test
    void softDeleteMember() {
        // given
        LocalDate now = LocalDate.now();

        Role role = buildRole(Authentication.USER.getName());
        Member member = buildMember("test", role);

        AppUser mockUser = mock(AppUser.class);
        RoleResponse mockRoleResponse = mock(RoleResponse.class);

        when(mockUser.getRole()).thenReturn(mockRoleResponse);
        when(mockRoleResponse.getRoleName()).thenReturn(Authentication.USER.getName());

        // when
        when(mockRepository.findById(any()))
                .thenReturn(Optional.of(member));

        service.softDeleteMember(mockUser, now);

        // then
        assertThat(member)
                .extracting("isUse", "deletedDate")
                .containsExactly(Boolean.FALSE, now);

    }

    @DisplayName("사용자 조회(ALL)")
    @Test
    void findAllMembers() {
        // given
        Member member1 = buildMember("test1", USER);
        Member member2 = buildMember("test2", USER);
        Member member3 = buildMember("test3", USER);
        // when
        when(mockRepository.findAll()).thenReturn(List.of(member1, member2, member3));

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

        when(mockRepository.findById(any()))
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

    @DisplayName("회원 저장 FOR PUBLIC")
    @Test
    void createMemberForPublic() {
        // given
        final String loginId = "test";
        final String password = "1234";
        final String confirmPassword = "1234";
        final String name = "test";

        PublicMemberCreateRequest request =
                buildPublicMemberCreateRequest(loginId, name, password, confirmPassword);

        when(mockRepository.save(any()))
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
        verify(mockRepository, times(1)).save(any());
    }

    @DisplayName("회원 수정 FOR USER")
    @Test
    void updateMemberForUser() {
        // given
        final String name = "test";
        final String password = "1234";
        final String confirmPassword = "1234";
        final Long id = 1L;

        UserMemberUpdateRequest request = buildUserMemberUpdateRequest(id, name, password, confirmPassword);

        when(mockRepository.findById(anyLong()))
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
    }

    @DisplayName("회원 수정 FOR ADMIN")
    @Test
    void updateMemberForAdmin() {
        // given
        final Long id = 1L;
        final boolean isUse = true;

        final Long loginMemberId = 2L;

        AdminMemberUpdateRequest request = buildAdminMemberUpdateRequest(id, Authentication.USER.getName(), isUse);

        when(mockRepository.findById(anyLong()))
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