package com.project.beauty_care.domain.member;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.PasswordMissMatchException;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MemberValidatorTest extends TestSupportWithOutRedis {
    @Autowired
    private MemberValidator validator;

    @DisplayName("권한 체크")
    @TestFactory
    Collection<DynamicTest> checkAdminRole() {
        return List.of(
                DynamicTest.dynamicTest("정상 시나리오", () -> {
                    assertDoesNotThrow(() -> validator.checkAdminRole(Authentication.USER.getName()));
                }),
                DynamicTest.dynamicTest("관리자 권한을 가진 사용자를 수정하려고 하면, 예외 발생", () -> {
                    assertThatThrownBy(() -> validator.checkAdminRole(Authentication.ADMIN.getName()))
                            .isInstanceOf(RequestInvalidException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.CAN_NOT_UPDATE_ADMIN_ROLE.getMessage());
                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.CAN_NOT_UPDATE_ADMIN_ROLE.getErrorCode());
                            });
                })
        );
    }

    @DisplayName("개인정보 수정 validation")
    @TestFactory
    Collection<DynamicTest> checkLoginUserEqualsRequest() {
        return List.of(
                DynamicTest.dynamicTest("정상 시나리오", () -> {
                    assertDoesNotThrow(() -> validator.checkLoginUserEqualsRequest(1L, 2L));
                }),
                DynamicTest.dynamicTest("개인정보 수정 시 예외 발생", () -> {
                    assertThatThrownBy(() -> validator.checkLoginUserEqualsRequest(1L, 1L))
                            .isInstanceOf(RequestInvalidException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.MUST_UPDATE_PRIVATE_PAGE.getMessage());
                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.MUST_UPDATE_PRIVATE_PAGE.getErrorCode());
                            });
                })
        );
    }

    @DisplayName("비밀번호와 비밀번호 확인이 일치하는지 체크")
    @TestFactory
    Collection<DynamicTest> validConfirmPassword() {
        return List.of(
                DynamicTest.dynamicTest(
                        "정상 시나리오",
                        () -> assertDoesNotThrow(() -> validator.validConfirmPassword("qwer1234", "qwer1234"))
                ),
                DynamicTest.dynamicTest("불일치 => 예외 발생",
                        () -> assertThatThrownBy(() -> validator.validConfirmPassword("qwer1234", "qwer12345"))
                                .isInstanceOf(PasswordMissMatchException.class)
                                .extracting("errors")
                                .satisfies(errors -> {
                                    assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.PASSWORD_MISS_MATCH.getMessage());
                                    assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.PASSWORD_MISS_MATCH.getErrorCode());
                                })
                )
        );
    }
}