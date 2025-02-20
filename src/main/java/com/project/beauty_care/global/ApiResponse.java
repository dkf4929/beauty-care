package com.project.beauty_care.global;

import com.project.beauty_care.global.enums.SuccessResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {
    private SuccessResult successResult;
    private HttpStatus httpStatus;
    private T data;

    public ApiResponse(SuccessResult successResult, HttpStatus httpStatus, T data) {
        this.successResult = successResult;
        this.httpStatus = httpStatus;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(SuccessResult successResult, HttpStatus httpStatus, T data) {
        return new ApiResponse<>(successResult, httpStatus, data);
    }
}
