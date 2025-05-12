package com.project.beauty_care;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public abstract class TestSupportWithRedis extends RedisConnectionSupport {
    @Autowired
    protected RedisConnectionFactory connectionFactory;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;
}
