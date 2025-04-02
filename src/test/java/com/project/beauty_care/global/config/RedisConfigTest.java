package com.project.beauty_care.global.config;

import com.project.beauty_care.TestSupportWithRedis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.testcontainers.shaded.org.apache.commons.lang3.ObjectUtils;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class RedisConfigTest extends TestSupportWithRedis {
    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DisplayName("redis ping test")
    @Test
    void connectRedis() {
        assertDoesNotThrow(() -> {
            RedisConnection connection = connectionFactory.getConnection();
            connection.ping();
            connection.close();
        }, "redis 연결 실패");
    }

    @DisplayName("redis put & delete")
    @Test
    void putAndDelete() {
        // given
        final String KEY = "key";
        final String VALUE = "value";

        putObjectSingle(KEY, VALUE);
        Object objectByRedis = getSingleObjectByKey(KEY);

        // value 값 일치하는지 확인
        assertThat(objectByRedis).isEqualTo(VALUE);

        // when
        // key 삭제
        redisTemplate.delete(KEY);

        Object deletedObjectByRedis = getSingleObjectByKey(KEY);

        // then
        assertThat(deletedObjectByRedis).isNull();
    }

    @DisplayName("key 만료 시, null을 리턴한다.")
    @Test
    void whenGetExpiredKeyReturnNull() {
        // given
        final String KEY = "key";
        final String VALUE = "value";

        putObjectSingle(KEY, VALUE);
        Object objectByRedis = getSingleObjectByKey(KEY);

        assertThat(objectByRedis).isEqualTo(VALUE);

        // when
        redisTemplate.expire(KEY, Duration.ZERO);

        await().atMost(Duration.ofSeconds(1))
                .until(() -> ObjectUtils.isEmpty(redisTemplate.opsForValue().get(KEY)));

        // then
        Object expiredObject = redisTemplate.opsForValue().get(KEY);
        assertThat(expiredObject).isNull();
    }

    @DisplayName("reids key clear")
    @Test
    void clearAllRedis() {
        // given
        final List<String> keyList = List.of("key1", "key2");
        final List<String> valueList = List.of("value1", "value2");

        putObjectMulti(keyList, valueList);
        List<Object> objectByRedis = getMultiObjectByKey(keyList);

        for (int i = 0; i < objectByRedis.size(); i++) {
            assertThat(objectByRedis.get(i)).isEqualTo(valueList.get(i));
        }

        clearAll();

        // when
        Set<String> keySet = redisTemplate.keys("*");

        // then
        assertThat(keySet).isEmpty();
    }

    private void putObjectMulti(List<String> key, List<String> value) {
        for (int i = 0; i < key.size(); i++) {
            redisTemplate.opsForValue().set(key.get(i), value.get(i));
        }
    }

    private List<Object> getMultiObjectByKey(List<String> keyList) {
        return redisTemplate.opsForValue().multiGet(keyList);
    }

    private void putObjectSingle(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    private Object getSingleObjectByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    private void clearAll() {
        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(100).build();

        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions)) {
            while (cursor.hasNext()) {
                redisTemplate.delete(new String(cursor.next()));
            }
        }
    }
}