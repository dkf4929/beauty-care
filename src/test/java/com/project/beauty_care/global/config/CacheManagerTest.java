package com.project.beauty_care.global.config;

import com.project.beauty_care.TestSupportWithRedis;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.repository.CodeRepository;
import com.project.beauty_care.domain.code.service.CodeService;
import com.project.beauty_care.global.enums.RedisCacheKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class CacheManagerTest extends TestSupportWithRedis {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CodeService codeService;

    @MockitoBean
    private CodeRepository codeRepository;

    @DisplayName("cache test")
    @Test
    void findCodeWithCache() {
        final String key = "ALL";

        Code code
                = buildCode("sys:agree", "동의 상태", 1, new ArrayList<>(), "", Boolean.TRUE);

        doReturn(Optional.of(code)).when(codeRepository).findByParentIsNull();

        // 최초 호출 -> DB 접근 (캐시 저장)
        codeService.findAllCode();
        verify(codeRepository, times(1)).findByParentIsNull();

        //  캐시 저장 여부 확인
        Cache cache = cacheManager.getCache(RedisCacheKey.CODE);
        assertThat(cache).isNotNull();

        Object cachedValue = cache.get(key);
        assertThat(cachedValue).isNotNull();

        // 캐시에서 조회
        codeService.findAllCode();
        verify(codeRepository, times(1)).findByParentIsNull(); // repository 접근 count 그대로 1

        assertAll(
                () -> assertThat(cache.get(key)).isNotNull(),
                () -> cache.evict(key),
                () -> assertThat(cache.get(key)).isNull()
        );

        // 캐시 삭제 후 호출 -> count 증가
        codeService.findAllCode();
        verify(codeRepository, times(2)).findByParentIsNull();
    }

    private Code buildCode(String id,
                           String name,
                           Integer sortNumber,
                           List<Code> children,
                           String description,
                           Boolean isUse) {
        return Code.builder()
                .id(id)
                .name(name)
                .sortNumber(sortNumber)
                .children(children)
                .description(description)
                .isUse(isUse)
                .build();
    }
}