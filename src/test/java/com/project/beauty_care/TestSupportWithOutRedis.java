package com.project.beauty_care;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/*
* Redis 비활성화 test
*/
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.cache.type=none",
        "spring.data.redis.host=test", // 실제 연결 x -> 없으면 에러남..
        "spring.data.redis.port=1234"
})
public abstract class TestSupportWithOutRedis extends DataBaseConnectionSupport {
    @MockitoBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockitoBean
    private CacheManager cacheManager;
}
