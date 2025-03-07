package com.project.beauty_care.domain.member;

import com.project.beauty_care.domain.member.dto.MemberCreateRequest;
import com.project.beauty_care.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member createMember(MemberCreateRequest request) {
        // dto to entity
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(encodePassword(request.getPassword()))
                .name(request.getName())
                .role(Role.USER)
                .build();

        return repository.save(member);
    }

    // 비밀번호 암호화
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
