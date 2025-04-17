package com.project.beauty_care.domain.member;

import com.project.beauty_care.domain.mapper.MemberMapper;
import com.project.beauty_care.domain.member.dto.AdminMemberCreateRequest;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.dto.MemberSummaryResponse;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
import com.project.beauty_care.domain.role.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberConverter {
    public Member buildEntity(PublicMemberCreateRequest request, String password, Role role) {
        return Member.builder()
                .loginId(request.getLoginId())
                .password(password)
                .name(request.getName())
                .role(role)
                .build();
    }

    public Member buildEntity(AdminMemberCreateRequest request, String password, Role role) {
        return Member.builder()
                .loginId(request.getLoginId())
                .password(password)
                .name(request.getName())
                .role(role)
                .build();
    }

    public static MemberResponse toResponse(Member member) {
        return MemberMapper.INSTANCE.toResponse(member);
    }

    public List<MemberSummaryResponse> toSummaryResponse(List<Member> memberList) {
        return memberList.stream()
                .map(MemberMapper.INSTANCE::toSummaryResponse)
                .toList();
    }
}
