package com.project.beauty_care.domain.menu;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.menu.dto.AdminMenuCreateRequest;
import com.project.beauty_care.domain.menu.dto.MenuBaseRequest;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static org.mockito.Mockito.*;

class MenuValidatorTest extends TestSupportWithOutRedis {
    @Autowired
    private MenuValidator validator;

    @Mock
    private Menu parent;

    @DisplayName("메뉴 계층 validation 시나리오")
    @TestFactory
    Collection<DynamicTest> validateMenuLevelAndIsLeaf() {
        return List.of(
                dynamicTest("정상 시나리오", () -> {
                    // given
                    AdminMenuCreateRequest request = buildCreateRequest(3, Boolean.TRUE);

                    // when, then
                    assertDoesNotThrow(() -> validator.validateMenuLevelAndIsLeaf(request));
                }),
                dynamicTest("최상위 메뉴를 leaf 메뉴로 지정할 경우, 예외 발생", () -> {
                    // given
                    AdminMenuCreateRequest request = buildCreateRequest(1, Boolean.TRUE);

                    // when, then
                    assertThatThrownBy(() -> validator.validateMenuLevelAndIsLeaf(request))
                            .isInstanceOf(RequestInvalidException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.CAN_NOT_BE_LEAF_MENU.getMessage());
                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.CAN_NOT_BE_LEAF_MENU.getErrorCode());
                            });
                }),
                dynamicTest("최하위 메뉴를 leaf 메뉴로 지정하지 않을 경우 예외 발생", () -> {
                    // given
                    AdminMenuCreateRequest request = buildCreateRequest(3, Boolean.FALSE);

                    // when, then
                    assertThatThrownBy(() -> validator.validateMenuLevelAndIsLeaf(request))
                            .isInstanceOf(RequestInvalidException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.MAX_MENU_DEPTH_ERROR.getMessage());
                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.MAX_MENU_DEPTH_ERROR.getErrorCode());
                            });
                })
        );
    }

    @DisplayName("상위 메뉴 사용 중 상태인지 검증")
    @TestFactory
    Collection<DynamicTest> validateParentMenuIsUse() {
        return List.of(
                dynamicTest("정상 시나리오", () -> {
                    // given
                    when(parent.getIsUse()).thenReturn(Boolean.TRUE);

                    MenuBaseRequest request = buildCreateRequestOnlyIsUse();

                    // when, then
                    assertDoesNotThrow(() -> validator.validateParentMenuIsUse(request, parent));
                }),
                dynamicTest("상위 메뉴 사용 불가 & 하위 메뉴 사용 => 예외 발생", () -> {
                    // given
                    when(parent.getIsUse()).thenReturn(Boolean.FALSE);

                    MenuBaseRequest request = buildCreateRequestOnlyIsUse();

                    // when, then
                    assertThatThrownBy(() -> validator.validateParentMenuIsUse(request, parent))
                            .isInstanceOf(RequestInvalidException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.PARENT_MENU_NOT_USE.getMessage());
                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.PARENT_MENU_NOT_USE.getErrorCode());
                            });
                })
        );
    }

    @DisplayName("상위 메뉴가 leaf menu 아닌지 검증")
    @TestFactory
    Collection<DynamicTest> validateParentMenuIsLeafFalse() {
        return List.of(
                dynamicTest("정상 시나리오", () ->
                        assertDoesNotThrow(() -> validator.validateParentMenuIsLeafFalse(Boolean.FALSE))
                ),
                dynamicTest("상위 메뉴가 leaf menu => 예외", () -> {
                    // when, then
                    assertThatThrownBy(() -> validator.validateParentMenuIsLeafFalse(Boolean.TRUE))
                            .isInstanceOf(RequestInvalidException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.MAX_MENU_DEPTH_ERROR.getMessage());
                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.MAX_MENU_DEPTH_ERROR.getErrorCode());
                            });
                })
        );
    }

    private AdminMenuCreateRequest buildCreateRequest(Integer menuLevel, Boolean isLeaf) {
        return AdminMenuCreateRequest.builder()
                .menuLevel(menuLevel)
                .isLeaf(isLeaf)
                .build();
    }

    private MenuBaseRequest buildCreateRequestOnlyIsUse() {
        return MenuBaseRequest.builder().isUse(Boolean.TRUE).build();
    }
}
