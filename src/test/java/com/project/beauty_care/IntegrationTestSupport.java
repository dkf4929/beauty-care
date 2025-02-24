package com.project.beauty_care;

import com.project.beauty_care.global.config.DynamicActiveProfilesResolver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(resolver = DynamicActiveProfilesResolver.class)
@SpringBootTest
public abstract class IntegrationTestSupport {
}
