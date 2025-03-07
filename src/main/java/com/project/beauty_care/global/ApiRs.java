package com.project.beauty_care.global;

import com.project.beauty_care.global.enums.SuccessResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
// 스웨거 어노테이션 중복으로, 명칭 변경
public class ApiRs<T> {
    private SuccessResult successResult;
    private HttpStatus httpStatus;
    private T data;

    public ApiRs(SuccessResult successResult, HttpStatus httpStatus, T data) {
        this.successResult = successResult;
        this.httpStatus = httpStatus;
        this.data = data;
    }

    public static <T> ApiRs<T> success(SuccessResult successResult, HttpStatus httpStatus, T data) {
        return new ApiRs<>(successResult, httpStatus, data);
    }
}
