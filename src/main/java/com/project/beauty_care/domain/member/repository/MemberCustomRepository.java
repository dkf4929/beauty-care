package com.project.beauty_care.domain.member.repository;

import com.project.beauty_care.domain.member.dto.MemberRoleResponse;

public interface MemberCustomRepository {
    public MemberRoleResponse findByLoginIdAndIsUseIsTrueFetch(String loginId);
}
