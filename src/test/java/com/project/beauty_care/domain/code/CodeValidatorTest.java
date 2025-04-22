package com.project.beauty_care.domain.code;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodeValidatorTest extends TestSupportWithOutRedis {
    @Autowired
    private CodeValidator validator;

    @Mock
    private Code code;

    @DisplayName("코드 삭제 가능여부 체크")
    @TestFactory
    Collection<DynamicTest> checkIsDeletable() {
        return List.of(
                DynamicTest.dynamicTest("정상 시나리오", () -> {
                    // given
                    when(code.getChildren())
                            .thenReturn(Collections.emptyList());

                    // when, then
                    assertDoesNotThrow(() -> validator.checkIsDeletable(code));
                }),
                DynamicTest.dynamicTest("하위코드 존재 => 예외 발생", () -> {
                    // given
                    when(code.getChildren())
                            .thenReturn(List.of(buildCode()));

                    // when, then
                    assertThatThrownBy(() -> validator.checkIsDeletable(code))
                            .isInstanceOf(RequestInvalidException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.CAN_NOT_DELETE_CODE.getMessage());
                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.CAN_NOT_DELETE_CODE.getErrorCode());
                            });
                })
        );
    }

    private Code buildCode() {
        return Code.builder().build();
    }
}