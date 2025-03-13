package com.project.beauty_care.global;

import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(name = "ApiResponse", description = "공통 API 응답")
// 스웨거 어노테이션 중복으로, 명칭 변경
public class SuccessResponse<T> {
    @Schema(description = "응답 메시지")
    private String message;

    @Schema(description = "응답 코드")
    private String code;

    @Schema(description = "응답 데이터")
    private T data;

    public SuccessResponse(SuccessCodes successCodes, HttpStatus httpStatus, T data) {
        this.message = successCodes.getMessage();
        this.code = successCodes.getCode();
        this.data = data;
    }

    public static <T> SuccessResponse<T> success(SuccessCodes successCodes, HttpStatus httpStatus, T data) {
        return new SuccessResponse<>(successCodes, httpStatus, data);
    }
}
