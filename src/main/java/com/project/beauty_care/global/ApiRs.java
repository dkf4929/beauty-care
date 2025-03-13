package com.project.beauty_care.global;

import com.project.beauty_care.global.enums.SuccessResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
// 스웨거 어노테이션 중복으로, 명칭 변경
public class ApiRs<T> {
    @Schema(description = "응답 메시지")
    private String message;

    @Schema(description = "응답 코드")
    private String code;

    @Schema(description = "응답 데이터")
    private T data;

    public ApiRs(SuccessResult successResult, HttpStatus httpStatus, T data) {
        this.message = successResult.getMessage();
        this.code = successResult.getCode();
        this.data = data;
    }

    public static <T> ApiRs<T> success(SuccessResult successResult, HttpStatus httpStatus, T data) {
        return new ApiRs<>(successResult, httpStatus, data);
    }
}
