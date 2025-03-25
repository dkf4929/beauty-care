package com.project.beauty_care.domain.code.service;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.dto.AdminCodeResponse;
import com.project.beauty_care.domain.code.repository.CodeRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodeServiceTest extends IntegrationTestSupport {
    @Autowired
    private CodeService service;

    @MockitoBean
    private CodeRepository repository;

    @DisplayName("계층형 코드 조회")
    @Test
    void findAllCode() {
        // given
        Code child = buildCode("sys:agree", "동의 여부", 1, new ArrayList<>());
        Code child2 = buildCode("sys:cancel", "취소 여부", 2, new ArrayList<>());
        Code parent = buildCode("sys", "시스템", 1, List.of(child, child2));

        when(repository.findByParentIsNull())
                .thenReturn(Optional.ofNullable(parent));

        // when
        AdminCodeResponse response = service.findAllCode();

        // then
        assertThat(response.getChildren())
                .hasSize(2)
                .extracting("id", "name", "sortNumber")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("sys:agree", "동의 여부", 1),
                        Tuple.tuple("sys:cancel", "취소 여부", 2)
                );

        verify(repository, times(1)).findByParentIsNull();
    }

    private Code buildCode(String id, String name, Integer sortNumber, List<Code> children) {
        return Code.builder()
                .id(id)
                .name(name)
                .sortNumber(sortNumber)
                .children(children)
                .build();
    }
}