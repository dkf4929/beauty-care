package com.project.beauty_care.global.utils;

import com.project.beauty_care.TestSupportWithRedis;
import com.project.beauty_care.domain.menu.Menu;
import com.project.beauty_care.domain.menu.dto.AdminMenuResponse;
import com.project.beauty_care.domain.menu.repository.MenuRepository;
import com.project.beauty_care.domain.menu.service.MenuService;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.RedisCacheKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisUtilsTest extends TestSupportWithRedis {
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MenuService menuService;

    @MockitoBean
    private MenuRepository menuRepository;

    @DisplayName("레디스 키 clear")
    @Test
    void clearRedisByKey() {
        final List<String> keyList = List.of("test1", "test2");
        final List<String> valueList = List.of("1", "2");

        redisUtils.putRedisCache(keyList.get(0), valueList.get(0));
        redisUtils.putRedisCache(keyList.get(1), valueList.get(1));

        List<String> redisValueList = redisUtils.getRedisValue(keyList).stream()
                .map(String::valueOf)
                .toList();

        assertThat(redisValueList).isEqualTo(valueList);
    }

    @DisplayName("캐시 삭제")
    @Test
    void clearCacheByKey() {
        // given
        Menu menu = Menu.builder()
                .menuName("TOP")
                .isLeaf(Boolean.FALSE)
                .build();

        when(menuRepository.findByParentIsNull())
                .thenReturn(Optional.ofNullable(menu));

        AdminMenuResponse redisSavedValue = menuService.findAllMenu(Authentication.ADMIN.getName());

        // when
        Object redisCacheValue = redisUtils.getRedisCacheValue(RedisCacheKey.MENU, Authentication.ADMIN.getName());

        assertThat(redisCacheValue).isEqualTo(redisSavedValue);
        redisUtils.clearCacheByKey(RedisCacheKey.MENU, List.of(Authentication.ADMIN.getName()));

        Object afterDeleteCacheValue = redisUtils.getRedisCacheValue(RedisCacheKey.MENU, Authentication.ADMIN.getName());
        // then
        assertThat(afterDeleteCacheValue).isNull();
    }
}