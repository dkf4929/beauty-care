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
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicReference<Set<String>> extensionSet = new AtomicReference<>(Set.of("txt"));

        return List.of(
                dynamicTest("정상 시나리오", () -> {
                    final String extension = "txt";

                    assertDoesNotThrow(() -> validator.validExtension(extensionSet.get(), extension));
                }),
                dynamicTest("유효하지 않은 확장자 -> 예외발생", () -> {
                    assertExtension(extensionSet.get());
                }),
                dynamicTest("사용 가능한 확장자 없음 -> 예외발생", () -> {
                    // given
                    extensionSet.set(Set.of());

                    // then
                    assertExtension(extensionSet.get());
                })
        );
    }

    private void assertExtension(Set<String> extionsionSet) {
        final String extension = "gif";

        assertThatThrownBy(() -> validator.validExtension(extionsionSet, extension))
                .isInstanceOf(FileUploadException.class)
                .extracting("errors")
                .satisfies(errors -> {
                    assertThat(errors).hasFieldOrPropertyWithValue("message", Errors.NOT_SUPPORTED_EXTENSION.getMessage());
                    assertThat(errors).hasFieldOrPropertyWithValue("errorCode", Errors.NOT_SUPPORTED_EXTENSION.getErrorCode());
                });
    }
}