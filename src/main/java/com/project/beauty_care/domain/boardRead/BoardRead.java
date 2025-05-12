package com.project.beauty_care.domain.boardRead;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "board_read", timeToLive = 60 * 60 * 24)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardRead {
    @Id
    private String id;

    public static BoardRead from(String id) {
        BoardRead entity = new BoardRead();

        entity.id = id;

        return entity;
    }
}
