package com.project.beauty_care.global.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
public enum PermitSvc {
    SWAGGER("/beauty-care/swagger-ui/", "/**/swagger-ui/**", "swagger"),
    FAVICON("/favicon.ico", "/favicon.ico.*", "favicon"),
    SWAGGER_RESOURCE("/swagger-resources/", "/swagger-resources/**", "swagger resource"),
    SWAGGER_API("/v3/api-docs/", "/v3/api-docs/**", "swagger api"),
    LOGIN("/login", "", "로그인"),
    PUBLIC("/public", "/public/**", "회원가입"),
    HEALTH("/health", "", "health check");
    ;

    private final String path;
    private final String regex;
    private final String description;

    PermitSvc( String path, String regex, String description) {
        this.path = path;
        this.regex = regex;
        this.description = description;
    }

    public static String[] toArrayPath() {
        return Arrays.stream(values())
                .map(PermitSvc::getPath)
                .toArray(String[]::new);
    }

    public static String[] toRegex() {
        return Arrays.stream(values())
                .filter(permitSvc -> StringUtils.isNotEmpty(permitSvc.getRegex()))
                .map(PermitSvc::getRegex)
                .toArray(String[]::new);
    }
}
