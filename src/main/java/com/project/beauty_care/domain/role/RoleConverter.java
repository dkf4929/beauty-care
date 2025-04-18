package com.project.beauty_care.domain.role;

import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.member.dto.MemberSummaryResponse;
import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menuRole.MenuRole;
import com.project.beauty_care.domain.role.dto.RoleCreateRequest;
import com.project.beauty_care.domain.role.dto.RoleMemberResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RoleConverter {
    public List<RoleResponse> toResponseWithMenu(Menu menu) {
        return menu.getMenuRole().stream()
                .map(MenuRole::getRole)
                .map(role -> RoleMapper.INSTANCE.toResponse(role, RoleResponse.patternMapToList(role.getUrlPatterns())))
                .toList();
    }

    public RoleResponse toResponse(Role role, List<String> urlPatterns) {
        return RoleMapper.INSTANCE.toResponse(role, urlPatterns);
    }

    public RoleResponse toResponse(Role role) {
        return RoleMapper.INSTANCE.toSimpleResponse(role);
    }

    public Role buildEntity(RoleCreateRequest request, Map<String, Object> patternMap) {
        return Role.builder()
                .roleName(request.getRoleName().toUpperCase())
                .urlPatterns(patternMap)
                .isUse(request.getIsUse())
                .build();
    }

    public RoleMemberResponse toResponse(Role role, List<String> urlPatterns, List<MemberSummaryResponse> summaryResponseList) {
        return RoleMapper.INSTANCE.toResponse(role, urlPatterns, summaryResponseList);
    }
}
