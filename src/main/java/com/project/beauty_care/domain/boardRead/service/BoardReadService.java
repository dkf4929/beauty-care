package com.project.beauty_care.domain.boardRead.service;

import com.project.beauty_care.domain.boardRead.BoardRead;
import com.project.beauty_care.domain.boardRead.repository.BoardReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardReadService {
    private final BoardReadRepository repository;

    public int getReadCountAndSaveRedis(Long boardId, Long memberId, Integer readCount) {
        String id = convertRedisId(boardId, memberId);
        boolean isExists = repository.existsById(id);

        // 24시간 내에 count 올라간 내역이 있을 경우
        // 그대로 리턴
        if (isExists) return readCount;

        BoardRead entity = BoardRead.from(id);

        repository.save(entity);

        return ++readCount;
    }

    private String convertRedisId(Long boardId, Long memberId) {
        return boardId + ":" + memberId;
    }
}
