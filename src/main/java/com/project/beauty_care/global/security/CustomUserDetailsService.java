package com.project.beauty_care.global.security;

import com.project.beauty_care.domain.mapper.RoleMapper;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.global.security.dto.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository repository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = repository.findByLoginIdAndIsUseIsTrue(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("등록된 사용자가 아닙니다."));

        Role role = member.getRole();

        // 인증용 객체로 변환
        return AppUser.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .role(RoleMapper.INSTANCE.toResponse(role, RoleResponse.patternMapToList(role.getUrlPatterns())))
                .build();
    }
}
