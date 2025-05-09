package com.project.beauty_care.domain.attachFile;

import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.FileUploadException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AttachFileValidator {
    public void validExtension(Set<String> extensionSet, String extension) {
        if (!extensionSet.contains(extension))
            throw new FileUploadException(Errors.NOT_SUPPORTED_EXTENSION);
    }
}
