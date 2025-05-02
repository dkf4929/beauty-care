package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCodes {
    // login
    LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),

    // CRUD
    SAVE_SUCCESS(HttpStatus.CREATED, "저장이 완료 되었습니다."),
    DELETE_SUCCESS(HttpStatus.NO_CONTENT, "삭제 되었습니다."),
    CANCEL_SUCCESS(HttpStatus.NO_CONTENT, "탈퇴 완료 되었습니다."),
    RETRIEVE_SUCCESS(HttpStatus.OK, "조회가 완료 되었습니다."),
    UPDATE_SUCCESS(HttpStatus.OK, "수정 완료 되었습니다."),

    // member
    MEMBER_SAVE_SUCCESS(HttpStatus.CREATED, "회원 저장이 완료 되었습니다."),
    FILE_UPLOAD_SUCCESS(HttpStatus.CREATED, "파일 업로드 완료 되었습니다."),

    // scheduler
    SCHEDULER_EXECUTE_SUCCESS(HttpStatus.NO_CONTENT, "스케줄러 실행이 완료 되었습니다.")
    ;

    private final HttpStatus code;
    private final String message;
}
