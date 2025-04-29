package com.project.beauty_care.domain.member.service;

import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.MemberConverter;
import com.project.beauty_care.domain.member.MemberValidator;
import com.project.beauty_care.domain.member.dto.*;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.repository.RoleRepository;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.RequestInvalidException;
import com.project.beauty_care.global.security.dto.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {
    private final MemberRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberValidator validator;
    private final MemberConverter converter;
    @Value("${initial.password}")
    private String initialPassword;

    public Member createMemberPublic(PublicMemberCreateRequest request) {
        // dto to entity
        validator.validConfirmPassword(request.getPassword(), request.getConfirmPassword());

        Role role = findRoleById(Authentication.USER.getName());

        Member member = converter.buildEntity(request, encodePassword(request.getPassword()), role);

        return repository.save(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAllMembers() {
        List<Member> memberList = repository.findAll();

        return memberList.stream()
                .map(MemberConverter::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberById(Long id) {
        Member member = findById(id);

        return MemberConverter.toResponse(member);
    }

    // 회원 탈퇴 정책 -> 1년 개인정보 보호
    // 스케줄러 돌면서 탈퇴한지 1년 지난 회원 hardDelete 처리
    public Long softDeleteMember(AppUser appUser, LocalDate now) {
        Member findMember = findById(appUser.getMemberId());

        String role = appUser.getRole().getRoleName();

        // 관리자 권한 탈퇴 불가
        validator.validIsAdminAccount(role);

        // isUse => false & deletedDate => now
        findMember.softDelete(now);

        log.info("soft delete member = {}, {}", findMember.getId(), now);

        return findMember.getId();
    }

    public void hardDeleteMemberWhenAfterOneYear(LocalDate date) {
        // + 1년
        LocalDate expiredDate = date.plusYears(1);

        // 탈퇴한 지 1년이 지난 사용자를 찾는다.
        List<Member> memberList = repository.findAllByIsUseIsFalseAndDeletedDate(expiredDate);

        // 삭제
        repository.deleteAllInBatch(memberList);

        List<Long> idList = memberList.stream()
                .map(Member::getId)
                .toList();

        if (!idList.isEmpty()) log.info("delete Member id : {}", idList);
    }

    // 탈퇴 취소
    public void deleteCancel(AppUser appUser, LocalDate now) {
        // 탈퇴 취소 가능 기간 => 14일
        Optional<Member> memberOptional = repository.findByIdAndIsUseIsFalseAndDeletedDateBetween(
                appUser.getMemberId(),
                now.minusDays(14),
                now);

        if (memberOptional.isEmpty())
            throw new RequestInvalidException(Errors.CAN_NOT_DELETE_CANCEL_AFTER_14_DAYS);

        Member member = memberOptional.get();

        // isUse => true & deletedDate => null
        member.deleteCancel();
    }

    public Member createMemberAdmin(AdminMemberCreateRequest request) {
        Role role = findRoleById(request.getRole());

        Member member = converter.buildEntity(request, encodePassword(initialPassword), role);

        return repository.save(member);
    }

    public MemberResponse updateMemberUser(UserMemberUpdateRequest request) {
        // 개인정보는 사용자용 API에서 수정한다.
        validator.validConfirmPassword(request.getPassword(), request.getConfirmPassword());

        Member findMember = findById(request.getId());

        findMember.updateMember(request.getName(), encodePassword(request.getPassword()));
        return MemberConverter.toResponse(findMember);
    }

    public MemberResponse updateMemberAdmin(AdminMemberUpdateRequest request, AppUser loginUser) {
        // 개인정보는 사용자용 API에서 수정한다.
        validator.checkLoginUserEqualsRequest(request.getId(), loginUser.getMemberId());

        Member findMember = findById(request.getId());

        // 관리자 계정은 직접 수정 불가
        validator.checkAdminRole(findMember.getRole().getRoleName());

        Role role = findRoleById(request.getRole());

        findMember.updateMember(request, role);
        return MemberConverter.toResponse(findMember);
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
}
