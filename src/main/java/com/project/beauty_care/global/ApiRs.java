package com.project.beauty_care.global;

import com.project.beauty_care.global.enums.SuccessResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
// 스웨거 어노테이션 중복으로, 명칭 변경
public class ApiRs<T> {
    private String message;
    private String code;
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
