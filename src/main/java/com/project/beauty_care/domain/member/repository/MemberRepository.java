package com.project.beauty_care.domain.member.repository;

import com.project.beauty_care.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginIdAndIsUseIsTrue(String loginId);

    void deleteByLoginId(String loginId);

    Optional<Member> findByIdAndIsUseIsTrue(Long id);
}
