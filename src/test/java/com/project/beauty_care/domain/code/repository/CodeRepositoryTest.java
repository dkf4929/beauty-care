package com.project.beauty_care.domain.code.repository;

import com.project.beauty_care.RepositoryTestSupport;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CodeRepositoryTest extends RepositoryTestSupport {
    @Autowired
    private CodeRepository repository;

    @DisplayName("최상위 코드 조회")
    @Test
    void findByParentIsNull() {
        // given
        Code parent =
                buildCode("sys", "시스템", null, "시스템", 1, Boolean.TRUE);

        repository.save(parent);

        Code middle =
                buildCode("sys:agree", "동의 상태", parent, "동의 상태", 1, Boolean.TRUE);

        repository.save(middle);

        Code bottom =
                buildCode("sys:agree:Y", "동의", middle, "동의", 1, Boolean.TRUE);

        Code bottom2 =
                buildCode("sys:agree:N", "미동의", middle, "미동의", 2, Boolean.TRUE);


        repository.saveAll(List.of(bottom, bottom2));

        // when
        Code parentCode = repository.findByParentIsNull()
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_CODE));

        // then
        assertThat(parentCode.getParent())
                .isNull();

        assertThat(parentCode)
                .extracting(Code::getName,
                        Code::getDescription,
                        Code::getId,
                        Code::getParent,
                        Code::getSortNumber,
                        Code::getIsUse
                )
                .containsExactly(
                        parent.getName(),
                        parent.getDescription(),
                        parent.getId(),
                        parent.getParent(),
                        parent.getSortNumber(),
                        parent.getIsUse()
                );
    }

    @DisplayName("코드 저장 시나리오")
    @TestFactory
    Collection<DynamicTest> saveTest() {
        return List.of(
                DynamicTest.dynamicTest("정상 case", () -> {
                    // given
                    Code code =
                            buildCode("sys", "시스템", null, "시스템", 1, Boolean.TRUE);

                    // when
                    repository.save(code);

                    Code findCode = repository.findById(code.getId())
                            .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_CODE));

                    // then
                    assertThat(code)
                            .extracting("id", "name", "parent", "description", "sortNumber", "isUse")
                            .containsExactly(findCode.getId(),
                                    findCode.getName(),
                                    findCode.getParent(),
                                    findCode.getDescription(),
                                    findCode.getSortNumber(),
                                    findCode.getIsUse());
                }),
                DynamicTest.dynamicTest("동일한 상위코드와 이름을 가질 경우, 예외 발생", () -> {
                    // given
                    final String constraint = "UQ_CODE_UPPER_ID_AND_NAME";

                    Code parent =
                            buildCode("sys", "시스템", null, "시스템", 1, Boolean.TRUE);

                    repository.saveAndFlush(parent);

                    Code child =
                            buildCode("sys:agree", "동의 상태", parent, "동의 상태", 1, Boolean.TRUE);

                    Code child2 =
                            buildCode("sys:agree2", "동의 상태", parent, "동의 상태", 2, Boolean.TRUE);

                    repository.saveAndFlush(child);

                    // when, then
                    assertThatThrownBy(() -> repository.saveAndFlush(child2))
                            .isInstanceOf(DataIntegrityViolationException.class)
                            .hasMessageContaining(constraint);
                })
        );
    }

    @DisplayName("특정 아이디가 존재하는지 확인")
    @Test
    void existsById() {
        // given
        final String existsId = "sys";
        final String notExistsId = "sys:agree";

        Code code =
                buildCode(existsId, "시스템", null, "", 1, Boolean.TRUE);

        repository.save(code);

        // when
        boolean byExists = repository.existsById(existsId);
        boolean byNotExists = repository.existsById(notExistsId);

        // then
        assertThat(byExists)
                .isTrue();

        assertThat(byNotExists)
                .isFalse();
    }

    private Code buildCode(String id,
                           String name,
                           Code parent,
                           String description,
                           Integer sortNumber,
                           Boolean isUse) {
        return Code.builder()
                .id(id)
                .name(name)
                .parent(parent)
                .description(description)
                .sortNumber(sortNumber)
                .isUse(isUse)
                .build();
    }
}