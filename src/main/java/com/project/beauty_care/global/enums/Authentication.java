package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Authentication {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    final String name;
}
