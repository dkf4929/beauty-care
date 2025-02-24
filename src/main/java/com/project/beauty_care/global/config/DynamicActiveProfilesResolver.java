package com.project.beauty_care.global.config;

import org.springframework.test.context.ActiveProfilesResolver;

public class DynamicActiveProfilesResolver implements ActiveProfilesResolver {

    @Override
    public String[] resolve(Class<?> testClass) {
        String activeProfile = System.getProperty("env.profile");
        if ("prod".equals(activeProfile)) {
            return new String[]{"test-prod"};
        } else {
            return new String[]{"test-local"};
        }
    }
}
