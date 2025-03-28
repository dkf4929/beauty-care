package com.project.beauty_care.global.login.service;

import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import com.project.beauty_care.global.login.dto.LoginRequest;
import com.project.beauty_care.global.security.dto.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AppUser login(LoginRequest loginRequest) {
        String loginId = loginRequest.getLoginId();
        String password = loginRequest.getPassword();

        // 회원 검색
        Member findMember = repository.findByLoginIdAndIsUseIsTrue(loginId)
                .orElseThrow(() -> new RequestInvalidException(Errors.ANONYMOUS_USER));

        // 패스워드 일치 여부 검사
        validPassword(password, findMember);
        updateLastLoginDateTime(findMember);

        return AppUser.builder()
                .memberId(findMember.getId())
                .loginId(findMember.getLoginId())
                .name(findMember.getName())
                .role(findMember.getRole())
                .build();
    }

    private void validPassword(String password, Member findMember) {
        if (!passwordEncoder.matches(password, findMember.getPassword())) {
            throw new RequestInvalidException(Errors.PASSWORD_MISS_MATCH);
        }
    }

    @Transactional
    protected void updateLastLoginDateTime(Member loginMember) {
        loginMember.updateLastLoginDateTime(LocalDateTime.now());
    }
}
