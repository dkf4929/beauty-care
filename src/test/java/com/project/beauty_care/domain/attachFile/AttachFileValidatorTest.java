package com.project.beauty_care.domain.attachFile;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.FileUploadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class AttachFileValidatorTest extends TestSupportWithOutRedis {
    @Autowired
    private AttachFileValidator validator;

    @DisplayName("파일 확장자 검증 시나리오")
    @TestFactory
    public Collection<DynamicTest> validExtensionScenario() {
        final Set<String> extionsionSet = Set.of("txt");

        return List.of(
                dynamicTest("정상 시나리오", () -> {
                    final String extension = "txt";

                    assertDoesNotThrow(() -> validator.validExtension(extionsionSet, extension));
                }),
                dynamicTest("예외 발생 케이스", () -> {
                    final String extension = "gif";

                    assertThatThrownBy(() -> validator.validExtension(extionsionSet, extension))
                            .isInstanceOf(FileUploadException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.NOT_SUPPORTED_EXTENSION.getMessage());
                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.NOT_SUPPORTED_EXTENSION.getErrorCode());
                            });
                })
        );
    }
}