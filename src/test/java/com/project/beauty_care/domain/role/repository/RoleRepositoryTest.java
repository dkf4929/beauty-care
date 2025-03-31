package com.project.beauty_care.domain.role.repository;

import com.project.beauty_care.RepositoryTestSupport;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.global.enums.Authentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class RoleRepositoryTest extends RepositoryTestSupport {
    @Autowired
    private RoleRepository repository;

    @DisplayName("권한과 일치하는 Role을 찾는다.")
    @Test
    void findAllByRoleName() {
        // given
        repository.save(buildRole(Authentication.ADMIN.getName(), Boolean.TRUE));

        final String role = Authentication.ADMIN.getName();

        // when
        List<Role> roles = repository.findAllByRoleName(role);

        // then
        assertThat(roles).hasSize(1)
                .extracting("roleName", "isUse")
                .containsExactly(tuple(role, Boolean.TRUE));
    }

    @DisplayName("존재하지 않는 roleName으로 조회하면 빈 리스트를 반환한다.")
    @Test
    void findAllByRoleNameWithInvalidRoleName() {
        // given, when
        List<Role> roles = repository.findAllByRoleName("INVALID_ROLE");

        // then
        assertThat(roles).isEmpty();
    }

    @DisplayName("동일한 권한명이 존재하는지 확인")
    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER", "MANAGER"})
    void existsByRoleNameAndIsUseIsTrue_ShouldReturnExpectedBoolean(String roleName) {
        // given
        repository.save(buildRole(roleName, Boolean.TRUE));

        // when
        Boolean isExists = repository.existsByRoleNameAndIsUseIsTrue(roleName);

        // then
        assertThat(isExists).isTrue();
    }

    @DisplayName("존재하지 않는 roleName으로 조회하면 false를 반환한다.")
    @Test
    void existsByRoleNameAndIsUseIsTrueWithInvalidRoleName() {
        // given, when
        Boolean isExists = repository.existsByRoleNameAndIsUseIsTrue("INVALID_ROLE");

        // then
        assertThat(isExists).isFalse();
    }

    @DisplayName("isUse null => return false")
    @Test
    void existsByRoleNameAndIsUseIsTrueWithEmptyIsUse() {
        // given
        repository.save(buildRole("TEST_ROLE", null));

        // when
        Boolean isExists = repository.existsByRoleNameAndIsUseIsTrue("TEST_ROLE");

        // then
        assertThat(isExists).isFalse();
    }

    @DisplayName("사용 중인 권한을 모두 조회한다.")
    @Test
    void findAllByIsUseIsTrue() {
        // given
        repository.save(buildRole(Authentication.ADMIN.getName(), Boolean.TRUE));
        repository.save(buildRole(Authentication.USER.getName(), Boolean.FALSE)); // 비활성화된 Role 추가

        // when
        List<Role> roles = repository.findAllByIsUseIsTrue();

        // then
        assertThat(roles).hasSize(1)
                .extracting("roleName", "isUse")
                .containsExactly(tuple(Authentication.ADMIN.getName(), Boolean.TRUE));
    }

    @DisplayName("isUse false => empty list")
    @Test
    void findAllByIsUseIsTrueMustReturnEmptyList() {
        // given
        repository.save(buildRole(Authentication.ADMIN.getName(), Boolean.FALSE));
        repository.save(buildRole(Authentication.USER.getName(), Boolean.FALSE));

        // when
        List<Role> roles = repository.findAllByIsUseIsTrue();

        // then
        assertThat(roles).isEmpty();
    }

    private Role buildRole(String roleName, Boolean isUse) {
        return Role.builder()
                .roleName(roleName)
                .isUse(isUse)
                .urlPatterns(Collections.emptyMap())
                .build();
    }
}
