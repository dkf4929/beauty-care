package com.project.beauty_care.global;

import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(description = "공통 API 응답")
public class SuccessResponse<T> {
    @Schema(description = "응답 코드")
    private int successCode;

    @Schema(description = "응답 메시지")
    private String successMessage;

    @Schema(description = "응답 데이터", implementation = Object.class)
    private T data;

    public SuccessResponse(SuccessCodes successCodes, HttpStatus successCode, T data) {
        this.successMessage = successCodes.getMessage();
        this.successCode = successCode.value();
        this.data = data;
    }

    public static <T> SuccessResponse<T> success(SuccessCodes successCodes, HttpStatus httpStatus, T data) {
        return new SuccessResponse<>(successCodes, httpStatus, data);
    }

    public static SuccessResponse success(SuccessCodes successCodes, HttpStatus httpStatus) {
        return new SuccessResponse<>(successCodes, httpStatus, null);
    }
}
