package com.project.beauty_care.domain.member.service;

import com.project.beauty_care.domain.mapper.MemberMapper;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.AdminMemberCreateRequest;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.domain.member.dto.PublicMemberCreateRequest;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;
    @Value("${initial.password}")
    private String initialPassword;

    @Transactional
    public Member createMemberPublic(PublicMemberCreateRequest request) {
        // dto to entity
        Member member = Member.createMember(request, encodePassword(request.getPassword()));

        return repository.save(member);
    }

    public List<MemberResponse> findAllMembers() {
        List<Member> memberList = repository.findAll();

        return memberList.stream()
                .map(MemberMapper.INSTANCE::toDto)
                .toList();
    }

    public MemberResponse findMemberById(Long id) {
        Member member = findById(id);

        return MemberMapper.INSTANCE.toDto(member);
    }

    @Transactional
    public void softDeleteMember(Long memberId) {
        Member findMember = findById(memberId);

        // isUse => false
        findMember.deleteMember();
    }

    @Transactional
    public Member createMemberAdmin(AdminMemberCreateRequest request) {
        Member member = Member.createMember(request, encodePassword(initialPassword));

        return repository.save(member);
    }

    // 비밀번호 암호화
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private Member findById(Long memberId) {
        return repository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_MEMBER));
    }
}
