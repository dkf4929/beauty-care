package com.project.beauty_care.domain.member;

import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.PasswordMissMatchException;
import com.project.beauty_care.global.exception.RequestInvalidException;
import org.springframework.stereotype.Component;

@Component
public class MemberValidator {
    public void checkAdminRole(String role) {
        if (role.equals(Authentication.ADMIN.getName()))
            throw new RequestInvalidException(Errors.CAN_NOT_UPDATE_ADMIN_ROLE);
    }

    public void checkLoginUserEqualsRequest(Long requestId, Long loginMemberId) {
        if (requestId.equals(loginMemberId))
            throw new RequestInvalidException(Errors.MUST_UPDATE_PRIVATE_PAGE);
    }

    public void validConfirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new PasswordMissMatchException(Errors.PASSWORD_MISS_MATCH);
    }

    public void validIsAdminAccount(String role) {
        if (role.equals(Authentication.ADMIN.getName()))
            throw new RequestInvalidException(Errors.CAN_NOT_DELETE_ADMIN_ACCOUNT);
    }
}
