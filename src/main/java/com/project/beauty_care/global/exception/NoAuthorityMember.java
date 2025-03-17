package com.project.beauty_care.global.exception;

import com.project.beauty_care.global.enums.Errors;

public class NoAuthorityMember extends CustomException {
    public NoAuthorityMember(Errors errors) {
        super(errors);
    }
}
