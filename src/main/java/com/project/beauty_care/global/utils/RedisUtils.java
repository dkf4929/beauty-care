package com.project.beauty_care.global.utils;

import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.SystemException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RedisUtils {
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    public void clearCacheByKey(String redisCacheKey, List<String> keyList) {
        if (StringUtils.isEmpty(redisCacheKey))
            throw new SystemException(Errors.NOT_FOUND_CODE);

        keyList.forEach(key -> Objects.requireNonNull(cacheManager.getCache(redisCacheKey)).evict(key));
    }

    public void putRedisCache(String redisCacheKey, String value) {
        redisTemplate.opsForValue().set(redisCacheKey, value);
    }

    public List<Object> getRedisValue(List<String> redisCacheKeyList) {
        return redisCacheKeyList.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .toList();
    }

    public Object getRedisCacheValue(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) return null;

        Object value = cache.get(key);
        if (value instanceof SimpleValueWrapper wrapper) return wrapper.get();

        return value;
    }
}
