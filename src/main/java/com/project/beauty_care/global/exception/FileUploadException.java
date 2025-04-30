package com.project.beauty_care.global.exception;

import com.project.beauty_care.global.enums.Errors;

public class FileUploadException extends CustomException {
    public FileUploadException(Errors errors) {
        super(errors);
    }
}
