package com.project.beauty_care.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Authentication {
    USER("USER"),
    ADMIN("ADMIN");

    final String name;
}
