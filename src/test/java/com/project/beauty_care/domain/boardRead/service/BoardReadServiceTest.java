package com.project.beauty_care.domain.boardRead.service;

import com.project.beauty_care.TestSupportWithRedis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class BoardReadServiceTest extends TestSupportWithRedis {
    @Autowired
    private BoardReadService service;

    private final String REDIS_KEY = "board_read";
    private static final long BOARD_ID = 1;
    private static final long MEMBER_ID = 1;

    @DisplayName("게시물 조회 카운트 증가 테스트")
    @Test
    void getReadCountAndSaveRedis() {
        //given
        final int readCount = 0;

        // when
        int increasedCount = service.getReadCountAndSaveRedis(BOARD_ID, MEMBER_ID, readCount);

        // then : 조회수 => 1 증가
        assertEquals(readCount + 1, increasedCount);
    }

    @DisplayName("redis ttl에 따른 조회 수 증가 테스트")
    @Test
    void increaseReadCountWithRedisTtl() {
        // 최초 호출 => 카운트 증가
        final int count = 0;

        int readCount = service.getReadCountAndSaveRedis(BOARD_ID, MEMBER_ID, count);

        assertEquals(count + 1, readCount);

        // 조회 카운트 = 1, redis o => 증가하지 않는다.
        readCount = service.getReadCountAndSaveRedis(BOARD_ID, MEMBER_ID, readCount);

        assertEquals(1, readCount);

        // redis ttl 초기화 => 카운트 증가
        String key = REDIS_KEY + ":" + BOARD_ID + ":" + MEMBER_ID;

        // ttl 초기화
        redisTemplate.expire(key, Duration.ZERO);

        readCount = service.getReadCountAndSaveRedis(BOARD_ID, MEMBER_ID, readCount);

        assertEquals(2, readCount);
    }
}