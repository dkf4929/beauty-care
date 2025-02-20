package com.project.beauty_care.domain.login;

import com.project.beauty_care.domain.login.dto.LoginRequestDto;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.MemberRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
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

    @Transactional
    public Member login(LoginRequestDto loginRequestDto) {
        String loginId = loginRequestDto.getLoginId();
        String password = loginRequestDto.getPassword();

        // 회원 검색
        Member findMember = repository.findByLoginId(loginId)
                .orElseThrow(() -> new RequestInvalidException(Errors.LOGIN_FAIL));

        // 패스워드 일치여부 검사
        boolean passwordMatch
                = passwordEncoder.matches(password, findMember.getPassword());

        // 불일치
        if (!passwordMatch)
            throw new RequestInvalidException(Errors.LOGIN_FAIL);

        // 현재 시간으로 로그인 시간 업데이트
        findMember.updateLastLoginDateTime(LocalDateTime.now());

        return findMember;
    }
}
