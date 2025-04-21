package com.project.beauty_care.domain.member;

import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.PasswordMissMatchException;
import org.springframework.stereotype.Component;

@Component
public class MemberValidator {
    public void checkAdminRole(String role) {
        if (role.equals(Authentication.ADMIN.getName()))
            throw new IllegalArgumentException("관리자 권한을 가진 사용자는 수정할 수 없습니다.");
    }

    public void checkLoginUserEqualsRequest(Long requestId, Long loginMemberId) {
        if (requestId.equals(loginMemberId))
            throw new IllegalArgumentException("개인정보 수정은 사용자 기능입니다.");
    }

    public void validConfirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new PasswordMissMatchException(Errors.PASSWORD_MISS_MATCH);
    }
}
