package com.project.beauty_care.domain.code.service;

import com.project.beauty_care.TestSupportWithRedis;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.dto.AdminCodeUpdateRequest;
import com.project.beauty_care.domain.code.repository.CodeRepository;
import com.project.beauty_care.global.enums.RedisCacheKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
        final String key = "all";

        Code code
                = buildCode("sys", "system", 1, new ArrayList<>(), "", Boolean.TRUE);

        Mockito.doReturn(Optional.of(code)).when(codeRepository).findByParentIsNull();

        // 최초 호출 -> DB 접근 (캐시 저장)
        codeService.findAllCode();
        verify(codeRepository, times(1)).findByParentIsNull();

        //  캐시 저장 여부 확인
        Cache cache = cacheManager.getCache(RedisCacheKey.CODE);
        assertThat(cache).isNotNull();

        Object cachedValue = cache.get(key, Object.class);
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

    @DisplayName("코드 수정 => 관련 캐시 삭제")
    @Test
    void whenUpdateCodeThenClearCache() {
        // given
        final String codeId = "sys";
        final String key = "all";

        Code code
                = buildCode(codeId, "시스템", 1, new ArrayList<>(), "", Boolean.TRUE);

        doReturn(Optional.of(code)).when(codeRepository).findByParentIsNull();
        doReturn(Optional.of(code)).when(codeRepository).findById(codeId);

        // 캐시 저장
        codeService.findAllCode();
        codeService.findCodeByIdCache(codeId);

        Cache cache = cacheManager.getCache(RedisCacheKey.CODE);

        // check cache
        assertThat(cache).isNotNull();
        assertThat(cache.get(codeId)).isNotNull();
        assertThat(cache.get(key)).isNotNull();

        // when
        codeService.updateCode(codeId, AdminCodeUpdateRequest.builder().isUse(Boolean.TRUE).build());

        // then : update -> 캐시 삭제
        assertThat(cache.get(codeId)).isNull();
        assertThat(cache.get(key)).isNull();
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