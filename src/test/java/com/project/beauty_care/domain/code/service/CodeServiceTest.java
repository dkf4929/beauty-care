package com.project.beauty_care.domain.code.service;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.dto.AdminCodeCreateRequest;
import com.project.beauty_care.domain.code.dto.AdminCodeResponse;
import com.project.beauty_care.domain.code.dto.AdminCodeUpdateRequest;
import com.project.beauty_care.domain.code.repository.CodeRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.*;

class CodeServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private CodeService service;

    @MockitoBean
    private CodeRepository repository;

    @DisplayName("계층형 코드 조회")
    @Test
    void findAllCode() {
        // given
        Code child =
                buildCode("sys:agree", "동의 여부", 1, new ArrayList<>(), "", Boolean.TRUE);

        Code child2 =
                buildCode("sys:cancel", "취소 여부", 2, new ArrayList<>(), "", Boolean.TRUE);

        Code parent =
                buildCode("sys", "시스템", 1, List.of(child, child2), "", Boolean.TRUE);

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

    @DisplayName("코드 수정")
    @Test
    void updateCode() {
        // given
        final String parentId = "sys";

        Code children
                = buildCode("sys:agree", "동의 상태", 1, new ArrayList<>(), "", Boolean.TRUE);

        Code parent
                = buildCode(parentId, "시스템", 1, List.of(children), "", Boolean.TRUE);

        AdminCodeUpdateRequest request = buildAdminUpdateRequest("시스템1", Boolean.FALSE, 2, "");

        when(repository.findById(any()))
                .thenReturn(Optional.ofNullable(parent));

        // when
        AdminCodeResponse response = service.updateCode(parentId, request);

        // then
        // 하위 코드 변경 되었는지 확인
        assertThat(children.getIsUse()).isFalse();

        assertThat(response)
                .extracting("name", "isUse", "sortNumber")
                .containsExactly("시스템1", Boolean.FALSE, 2);
    }

    @DisplayName("코드 삭제")
    @Test
    void deleteCode() {
        final String id = "sys";
        Code code
                = buildCode(id, "시스템", 1, new ArrayList<>(), "", Boolean.TRUE);

        when(repository.findById(any()))
                .thenReturn(Optional.ofNullable(code));

        // when
        String deletedId = service.deleteCode(id);

        // then
        verify(repository, times(1)).deleteById(any());
        assertThat(deletedId).isEqualTo(id);
    }

    @DisplayName("최상위 코드가 존재하지 않을 경우 빈 리스트를 리턴한다.")
    @Test
    void findAllCodeWithOutSuperCode() {
        // given
        when(repository.findByParentIsNull())
                .thenReturn(Optional.empty());

        final AdminCodeResponse emptyResponse
                = AdminCodeResponse.builder().build();

        // when
        AdminCodeResponse response = service.findAllCode();

        // then
        assertThat(response)
                .isEqualTo(emptyResponse);

        verify(repository, times(1)).findByParentIsNull();
    }

    @DisplayName("코드 저장 시나리오(service)")
    @TestFactory
    Collection<DynamicTest> createCode() {
        return List.of(
                dynamicTest("정상 case (상위 코드 x)", () -> {
                    // given
                    final String id = "sys";
                    final String name = "시스템";
                    final int sortNumber = 1;
                    final String description = "시스템";

                    AdminCodeCreateRequest request =
                            buildAdminCreateRequest(id, null, name, Boolean.TRUE, sortNumber, description);

                    when(repository.existsById(any()))
                            .thenReturn(Boolean.FALSE);

                    when(repository.save(any()))
                            .thenReturn(
                                    buildCode(id, name, sortNumber, new ArrayList<>(), description, Boolean.TRUE)
                            );

                    // when
                    AdminCodeResponse response = service.createCode(request);

                    // then
                    assertThat(response)
                            .extracting("id", "name", "sortNumber", "description")
                            .containsExactly(id, name, sortNumber, description);

                    verify(repository, times(1)).save(any());
                }),
                dynamicTest("동일한 ID로 저장시도 => 예외 발생", () -> {
                    // given
                    final String id = "sys:agree";
                    final String name = "동의 여부";
                    final int sortNumber = 1;

                    AdminCodeCreateRequest request =
                            buildAdminCreateRequest(id, null, name, Boolean.TRUE, sortNumber, "");

                    when(repository.existsById(any()))
                            .thenReturn(Boolean.TRUE);

                    // when, then
                    assertThatThrownBy(() -> service.createCode(request))
                            .isInstanceOf(RequestInvalidException.class)
                            .hasFieldOrPropertyWithValue("errors.message", Errors.DUPLICATED_CODE.getMessage())
                            .hasFieldOrPropertyWithValue("errors.errorCode", Errors.DUPLICATED_CODE.getErrorCode());
                })
        );
    }

    private AdminCodeCreateRequest buildAdminCreateRequest(String id,
                                                           String parentId,
                                                           String name,
                                                           Boolean isUse,
                                                           Integer sortNumber,
                                                           String description) {
        return AdminCodeCreateRequest.builder()
                .codeId(id)
                .parentId(parentId)
                .name(name)
                .isUse(isUse)
                .sortNumber(sortNumber)
                .description(description)
                .build();
    }

    private AdminCodeUpdateRequest buildAdminUpdateRequest(String name,
                                                           Boolean isUse,
                                                           Integer sortNumber,
                                                           String description) {
        return AdminCodeUpdateRequest.builder()
                .name(name)
                .isUse(isUse)
                .sortNumber(sortNumber)
                .description(description)
                .build();
    }

    private Code buildCode(String id,
                           String name,
                           Integer sortNumber,
                           List<Code> children,
                           String description,
                           Boolean isUse) {
        return Code.builder()
                .id(id)
                .name(name)
                .sortNumber(sortNumber)
                .children(children)
                .description(description)
                .isUse(isUse)
                .build();
    }
}