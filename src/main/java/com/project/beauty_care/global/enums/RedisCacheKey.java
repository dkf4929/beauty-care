package com.project.beauty_care.global.enums;

import lombok.Getter;

@Getter
public abstract class RedisCacheKey {
//    public static final String ALL_CODES = "all_codes";
    public static final String CODE = "code";
    public static final String CODE_PARENT = "code_parent";
    public static final String ROLE = "role";
    public static final String ROLE_EXISTS = "role_exists";
    public static final String MENU = "menu";
    public static final String MENU_ROLE = "menu_role";
}
