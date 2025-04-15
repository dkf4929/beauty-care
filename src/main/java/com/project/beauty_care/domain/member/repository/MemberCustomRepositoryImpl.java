package com.project.beauty_care.domain.member.repository;

import com.project.beauty_care.domain.mapper.MemberMapper;
import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.QMember;
import com.project.beauty_care.domain.member.dto.MemberRoleResponse;
import com.project.beauty_care.domain.role.QRole;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private final JPAQueryFactory queryFactory;
    private final QMember member;
    private final QRole role;

    public MemberCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
        this.member = QMember.member;
        this.role = QRole.role;
    }

    @Override
    public MemberRoleResponse findByLoginIdAndIsUseIsTrueFetch(String loginId) {
        Member findMember = queryFactory.selectFrom(member)
                .leftJoin(member.role, role).fetchJoin()
                .where(member.loginId.eq(loginId))
                .fetchOne();

        Role role = findMember.getRole();

        List<String> urlPatterns = Optional.ofNullable(role.getUrlPatterns().get("pattern"))
                .filter(List.class::isInstance)
                .map(list -> (List<?>) list)
                .orElse(Collections.emptyList())
                .stream()
                .map(String::valueOf)
                .toList();

        RoleResponse roleResponse = RoleMapper.INSTANCE.toResponse(role, urlPatterns);

        return MemberMapper.INSTANCE.toResponse(findMember, roleResponse);
    }
}
