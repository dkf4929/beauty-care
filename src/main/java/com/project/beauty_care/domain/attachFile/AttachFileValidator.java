package com.project.beauty_care.domain.attachFile;

import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Component
public class AttachFileValidator {
    public void validExtension(Set<String> extensionSet, String extension) {
        if (CollectionUtils.isEmpty(extensionSet) || !extensionSet.contains(extension))
            throw new FileUploadException(Errors.NOT_SUPPORTED_EXTENSION);
    }
}
