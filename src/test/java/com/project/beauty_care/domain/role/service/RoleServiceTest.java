package com.project.beauty_care.domain.role.service;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.dto.RoleCreateRequest;
import com.project.beauty_care.domain.role.dto.RoleMemberResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.dto.RoleUpdateRequest;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.*;

class RoleServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private RoleService service;

    @MockitoBean
    private RoleRepository repository;

    private static final String PATTERN = "pattern";

    final Role USER = buildRole(Authentication.USER.getName());
    final Role ADMIN = buildRole(Authentication.ADMIN.getName());

    @DisplayName("조건에 맞는 모든 권한을 조회한다.")
    @TestFactory
    Collection<DynamicTest> findAllRoles() {
        return List.of(
                dynamicTest("조회조건 없을 경우, 전체 조회", () -> {
                    // given
                    when(repository.findAll())
                            .thenReturn(List.of(USER, ADMIN));

                    // when
                    List<RoleMemberResponse> response = service.findAllRoles("");

                    // then
                    assertThat(response).hasSize(2)
                            .extracting("roleName")
                            .containsExactly("USER", "ADMIN");

                    verify(repository).findAll();
                }),
                dynamicTest("roleName과 일치하는 role 조회", () -> {
                    // given, when
                    when(repository.findAllByRoleName(Authentication.USER.getName()))
                            .thenReturn(List.of(USER));

                    when(repository.findAllByRoleName(Authentication.ADMIN.getName()))
                            .thenReturn(List.of(ADMIN));

                    List<RoleMemberResponse> userResponse = service.findAllRoles(Authentication.USER.getName());
                    List<RoleMemberResponse> adminResponse = service.findAllRoles(Authentication.ADMIN.getName());
                    
                    // then
                    assertResponse(userResponse, Authentication.USER.getName());
                    assertResponse(adminResponse, Authentication.ADMIN.getName());
                })
        );
    }

    @DisplayName("권한을 생성한다.")
    @Test
    void createRole() {
        // given
        RoleCreateRequest request = buildCreateRequest(Authentication.ADMIN.getName(), List.of("/admin/**"), Boolean.TRUE);

        when(repository.save(any(Role.class)))
                .thenReturn(Role.builder()
                        .roleName(request.getRoleName())
                        .urlPatterns(Map.of("pattern", request.getUrlPatterns()))
                        .isUse(request.getIsUse())
                        .build());

        // when
        RoleResponse response = service.createRole(request);

        // then
        assertThat(response)
                .isNotNull()
                .extracting("roleName", "urlPatterns", "isUse")
                .containsExactly(request.getRoleName(), request.getUrlPatterns(), request.getIsUse());
    }

    @DisplayName("동일한 roleName 으로 저장 시, 예외 발생")
    @Test
    void createRoleWithIdenticalRoleName() {
        // given
        when(repository.existsById(any()))
                .thenReturn(Boolean.TRUE);

        RoleCreateRequest request =
                buildCreateRequest(Authentication.ADMIN.getName(), List.of("/admin/**"), Boolean.TRUE);

        // when, then
        assertThatThrownBy(() -> service.createRole(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errors.message", Errors.DUPLICATED_ROLE.getMessage())
                .hasFieldOrPropertyWithValue("errors.errorCode", Errors.DUPLICATED_ROLE.getErrorCode());;
    }

    @DisplayName("패턴을 지정하지 않고, 저장한다.")
    @Test
    void createRoleWithUrlPatternEmpty() {
        // given
        RoleCreateRequest request = buildCreateRequest(Authentication.ADMIN.getName(), Collections.emptyList(), Boolean.TRUE);

        when(repository.save(any(Role.class)))
                .thenReturn(Role.builder()
                        .roleName(request.getRoleName())
                        .urlPatterns(Map.of())
                        .isUse(request.getIsUse())
                        .build());

        // when
        RoleResponse response = service.createRole(request);

        // then
        assertThat(response)
                .extracting("roleName", "urlPatterns", "isUse")
                .containsExactly(request.getRoleName(), request.getUrlPatterns(), request.getIsUse());
    }

    @DisplayName("일치하는 패턴의 Role을 검색한다.")
    @ParameterizedTest
    @CsvSource({"/admin/member", "/user/member", "/manager/test"})
    void findRoleNameByUrlPattern(String pattern) {
        // given
        final List<String> patternList = List.of("/admin/**", "/user/**", "/manager/test");

        when(repository.findAllByIsUseIsTrue())
                .thenReturn(List.of(
                                buildRole(
                                        Authentication.ADMIN.getName(),
                                        Map.of("pattern", patternList),
                                        Boolean.TRUE)
                        )
                );

        // when
        List<String> roleList = service.findRoleNameByUrlPattern(pattern);

        // then
        assertThat(roleList)
                .hasSize(1)
                .isEqualTo(List.of(Authentication.ADMIN.getName()));

        verify(repository, times(1)).findAllByIsUseIsTrue();
    }

    @DisplayName("일치하는 url 패턴이 존재하지 않을 경우 빈 리스트를 리턴한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/manager/member"})
    void findRoleNameByUrlPatternWithNotMatchedPattern(String pattern) {
        // given
        final List<String> patternList = List.of("/admin/**", "/user/**", "/manager/test");

        when(repository.findAllByIsUseIsTrue())
                .thenReturn(List.of(
                                buildRole(
                                        Authentication.ADMIN.getName(),
                                        Map.of("pattern", patternList),
                                        Boolean.TRUE)
                        )
                );

        // when
        List<String> roleList = service.findRoleNameByUrlPattern(pattern);

        // then
        assertThat(roleList).isEmpty();

        verify(repository, times(1)).findAllByIsUseIsTrue();
    }

    @DisplayName("권한 정보를 수정한다.")
    @Test
    void updateRole() {
        // given
        RoleUpdateRequest request =
                buildUpdateRequest("manager", "manager", List.of("/manager/**"), Boolean.TRUE);

        Role role = Role.builder().build();

        when(repository.findById(any()))
                .thenReturn(Optional.of(role));

        // when
        RoleResponse response = service.updateRole(request);

        // then
        assertThat(response)
                .extracting("roleName", "urlPatterns", "isUse")
                .containsExactly(request.getAfterRoleName(), request.getUrlPatterns(), request.getIsUse());
    }

    @DisplayName("권한명을 바꾸려고 할 때, 이미 해당 권한명이 존재하면 예외 발생")
    @Test
    void updateRoleByExistsRoleName() {
        // given
        RoleUpdateRequest request =
                buildUpdateRequest("MANAGER", "ADMIN", List.of("/manager/**"), Boolean.TRUE);

        when(repository.findById(any()))
                .thenReturn(Optional.of(Role.builder().build()));

        // when
        when(repository.existsById(any())).thenReturn(Boolean.TRUE);

        // then
        assertThatThrownBy(() -> service.updateRole(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errors.message", Errors.DUPLICATED_ROLE.getMessage())
                .hasFieldOrPropertyWithValue("errors.errorCode", Errors.DUPLICATED_ROLE.getErrorCode());
    }

    private Role buildRole(String role) {
        return Role.builder()
                .roleName(role)
                .urlPatterns(Collections.emptyMap())
                .members(Collections.emptyList())
                .build();
    }

    private Role buildRole(String role, Map<String, Object> urlPatterns, Boolean isUse) {
        return Role.builder()
                .roleName(role)
                .urlPatterns(urlPatterns)
                .isUse(isUse)
                .build();
    }

    private RoleCreateRequest buildCreateRequest(String roleName, List<String> urlPatterns, Boolean isUse) {
        return RoleCreateRequest.builder()
                .roleName(roleName)
                .urlPatterns(urlPatterns)
                .isUse(isUse)
                .build();
    }

    private RoleUpdateRequest buildUpdateRequest(String beforeRoleName,
                                                 String afterRoleName,
                                                 List<String> urlPatterns,
                                                 Boolean isUse) {
        return RoleUpdateRequest.builder()
                .beforeRoleName(beforeRoleName)
                .afterRoleName(afterRoleName)
                .urlPatterns(urlPatterns)
                .isUse(isUse)
                .build();
    }

    private void assertResponse(List<RoleMemberResponse> adminResponse, String authentication) {
        assertThat(adminResponse).hasSize(1)
                .extracting("roleName")
                .containsExactly(authentication);

        verify(repository, times(1)).findAllByRoleName(authentication);
    }
}