package com.project.beauty_care.global.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonUtils {
    private final CacheManager redisCacheManager;

    public void clearRedisCache(String cacheKey, String... keys) {
        for (String key : keys) {
            Cache cache = redisCacheManager.getCache(cacheKey);

            if (ObjectUtils.isNotEmpty(cache))
                cache.evict(key);
        }
    }
}
