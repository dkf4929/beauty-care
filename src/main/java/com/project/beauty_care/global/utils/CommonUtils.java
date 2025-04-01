package com.project.beauty_care.global.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CommonUtils {
    private final CacheManager redisCacheManager;

    public void clearRedisCache(String... keys) {
        for (String key : keys) {
            Objects.requireNonNull(redisCacheManager.getCache(key)).evict(key);
        }

//        redisCacheManager.getCache("dqwdqw").get()
    }

//    public
}
