package com.project.beauty_care.domain.menu.repository;

import com.project.beauty_care.domain.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuCustomRepository {
    Optional<Menu> findByParentIsNullAndIsUseIsTrue();
    Optional<Menu> findByParentIsNull();
}
