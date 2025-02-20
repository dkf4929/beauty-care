package com.project.beauty_care.global.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PermitSvc {
    SWAGGER("/beauty-care/swagger-ui/**", "/eng/swagger-ui.*", "swagger"),
    FAVICON("/favicon.ico", "/favicon.ico.*", "favicon"),
    SWAGGER_RESOURCE("/swagger-resources/**", "/swagger-resources.*", "swagger resource"),
    SWAGGER_API("/v3/api-docs/**", "/v3/api-docs.*", "swagger api"),
    LOGIN("/login", "", "로그인"),
    HEALTH("/health", "", "health check"),
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
        String[] result = new String[values().length];
        for (int i = 0; i < result.length; i++) {
            result[i] = values()[i].regex;
        }
        return result;
    }
}
