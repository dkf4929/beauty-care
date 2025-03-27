package com.project.beauty_care.domain.member.service;

import com.project.beauty_care.domain.mapper.MemberMapper;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.dto.*;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.PasswordMissMatchException;
import com.project.beauty_care.global.security.dto.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${initial.password}")
    private String initialPassword;

    public Member createMemberPublic(PublicMemberCreateRequest request) {
        // dto to entity
        validConfirmPassword(request.getPassword(), request.getConfirmPassword());

        Role role = findRoleById(Authentication.USER.getName());

        Member member = Member.createMember(request, encodePassword(request.getPassword()), role);

        return repository.save(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAllMembers() {
        List<Member> memberList = repository.findAll();

        return memberList.stream()
                .map(MemberMapper.INSTANCE::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberById(Long id) {
        Member member = findById(id);

        return MemberMapper.INSTANCE.toDto(member);
    }

    // 미사용
    public void softDeleteMember(Long memberId) {
        Member findMember = findById(memberId);

        // isUse => false
        findMember.deleteMember();
    }

    public Member createMemberAdmin(AdminMemberCreateRequest request) {
        Role role = findRoleById(request.getRole());

        Member member = Member.createMember(request, encodePassword(initialPassword), role);

        return repository.save(member);
    }

    public MemberResponse updateMemberUser(UserMemberUpdateRequest request) {
        // 개인정보는 사용자용 API에서 수정한다.
        validConfirmPassword(request.getPassword(), request.getConfirmPassword());

        Member findMember = findById(request.getId());

        findMember.updateMember(request.getName(), encodePassword(request.getPassword()));
        return MemberMapper.INSTANCE.toDto(findMember);
    }

    public MemberResponse updateMemberAdmin(AdminMemberUpdateRequest request, AppUser loginUser) {
        // 개인정보는 사용자용 API에서 수정한다.
        checkLoginUserEqualsRequest(request.getId(), loginUser.getMemberId());

        Member findMember = findById(request.getId());

        // 관리자 계정은 직접 수정 불가
        checkAdminRole(findMember.getRole());

        Role role = findRoleById(request.getRole());

        findMember.updateMember(request, role);
        return MemberMapper.INSTANCE.toDto(findMember);
    }

    // 비밀번호 암호화
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private Member findById(Long memberId) {
        return repository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_MEMBER));
    }

    private Role findRoleById(String roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_ROLE));
    }

    private void checkAdminRole(Role role) {
        if (role.getRoleName().equals(Authentication.ADMIN.getName()))
            throw new IllegalArgumentException("관리자 권한을 가진 사용자는 수정할 수 없습니다.");
    }

    private void checkLoginUserEqualsRequest(Long requestId, Long loginMemberId) {
        if (requestId.equals(loginMemberId))
            throw new IllegalArgumentException("개인정보 수정은 사용자 기능입니다.");
    }

    private void validConfirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new PasswordMissMatchException(Errors.PASSWORD_MISS_MATCH);
    }
}
