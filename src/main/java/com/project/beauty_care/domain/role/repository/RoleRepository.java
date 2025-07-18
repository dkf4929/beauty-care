package com.project.beauty_care.domain.role.repository;

import com.project.beauty_care.domain.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    public List<Role> findAllByRoleName(String roleName);

    public List<Role> findAllByIsUseIsTrue();

    public Boolean existsByRoleNameAndIsUseIsTrue(String roleName);
}
