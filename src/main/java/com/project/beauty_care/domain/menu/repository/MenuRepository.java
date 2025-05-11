package com.project.beauty_care.domain.menu.repository;

import com.project.beauty_care.domain.menu.Menu;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuCustomRepository {
    @EntityGraph(attributePaths = {"children"})
    Optional<Menu> findByParentIsNull();
    List<Menu> findByIsLeafIsTrueAndIsUseIsTrue();
}
