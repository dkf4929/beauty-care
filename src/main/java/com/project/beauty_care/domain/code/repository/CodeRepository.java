package com.project.beauty_care.domain.code.repository;

import com.project.beauty_care.domain.code.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, String> {
    Optional<Code> findByParentIsNull();
}
