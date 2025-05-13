package com.project.beauty_care.domain.board.service;

import com.project.beauty_care.domain.attachFile.AttachFileConverter;
import com.project.beauty_care.domain.attachFile.dto.AttachFileResponse;
import com.project.beauty_care.domain.attachFile.service.AttachFileService;
import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.board.BoardConverter;
import com.project.beauty_care.domain.board.BoardValidator;
import com.project.beauty_care.domain.board.dto.BoardCreateRequest;
import com.project.beauty_care.domain.board.dto.BoardCriteria;
import com.project.beauty_care.domain.board.dto.BoardResponse;
import com.project.beauty_care.domain.board.dto.BoardUpdateRequest;
import com.project.beauty_care.domain.board.repository.BoardRepository;
import com.project.beauty_care.domain.boardRead.service.BoardReadService;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.CodeConverter;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.code.service.CodeService;
import com.project.beauty_care.domain.enums.BoardType;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.exception.RequestInvalidException;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository repository;

    private final BoardConverter converter;
    private final AttachFileConverter fileConverter;
    private final CodeConverter codeConverter;

    private final BoardValidator validator;

    private final AttachFileService fileService;
    private final CodeService codeService;
    private final BoardReadService boardReadService;
    private final FileUtils fileUtils;

    @Transactional
    public BoardResponse createBoard(BoardCreateRequest request, AppUser user) {
        BoardType boardType = request.getBoardType();
        String role = user.getRole().getRoleName();

        validCreatedDateTime(user.getRole().getRoleName(), user.getMemberId());

        // 게시물 유형 validation check
        validator.validBoardType(boardType, role);

        // find code
        Code grade = codeService.findCodeById(request.getGrade());

        // dto -> entity
        Board entity = converter.buildEntity(request, grade);

        // save
        Board savedEntity = repository.save(entity);
        Long entityId = savedEntity.getId();

        List<AttachFileResponse> fileList = new ArrayList<>();

        // build file request
        if (!request.getAttachFiles().isEmpty()) {
            fileList = fileService.saveFileAndConvertResponse(request.getAttachFiles(), entityId, savedEntity);
        }

        return converter.toResponse(savedEntity, fileList, codeConverter.toResponse(grade));
    }

    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardUpdateRequest request) {
        Board entity = findById(boardId);

        // save
        Board savedEntity = repository.save(entity);
        Long entityId = savedEntity.getId();

        List<AttachFileResponse> fileList = new ArrayList<>();

        // build file request
        if (!request.getAttachFiles().isEmpty())
            fileList = fileService.saveFileAndConvertResponse(request.getAttachFiles(), entityId, savedEntity);

        entity.updateBoard(request);

        return converter.toResponse(savedEntity, fileList, codeConverter.toResponse(entity.getGrade()));
    }

    @Transactional
    public BoardResponse findBoardById(Long boardId, AppUser loginUser) {
        Board entity = findById(boardId);

        CodeResponse grade = codeConverter.toResponse(entity.getGrade());

        List<AttachFileResponse> fileList = entity.getAttachFiles().stream()
                .map(file -> {
                    String fileFullPath = fileUtils.extractFileFullPath(file);

                    return fileConverter.toResponse(file, fileFullPath);
                })
                .toList();

        int readCount = boardReadService.getReadCountAndSaveRedis(boardId,
                loginUser.getMemberId(), entity.getReadCount());

        // 조회 수 업데이트
        entity.updateReadCount(readCount);

        return converter.toResponse(entity, fileList, grade);
    }

    public Page<BoardResponse> findBoardAllPageByCriteria(BoardCriteria criteria, Pageable pageable) {
        Page<Board> result = repository.findAllByCriteriaPage(criteria, pageable);

        List<BoardResponse> contentList = result.stream()
                .map(board -> {
                    CodeResponse grade = codeConverter.toResponse(board.getGrade());

                    List<AttachFileResponse> fileList = board.getAttachFiles().stream()
                            .map(file -> {
                                String fileFullPath = fileUtils.extractFileFullPath(file);

                                return fileConverter.toResponse(file, fileFullPath);
                            })
                            .toList();

                    return converter.toResponse(board, fileList, grade);
                })
                .toList();

        return new PageImpl<>(contentList, pageable, result.getTotalElements());
    }

    private Board findById(Long boardId) {
        return repository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_BOARD));
    }

    // 관리자가 아닐 경우, 1분에 하나의 게시물만 작성 가능.
    private void validCreatedDateTime(String roleName, Long memberId) {
        if (!roleName.equals(Authentication.ADMIN.name())) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowMinusOneMinute = now.minusMinutes(1);

            // 1분 이내에 작성한 게시물이 있는지 찾는다.
            Boolean isExists =
                    repository.existsBoardByCreatedByAndCreatedDateTimeBetween(memberId, nowMinusOneMinute, now);

            // 있으면 예외
            if (isExists)
                throw new RequestInvalidException(Errors.MUST_WRITE_BOARD_AFTER_ONE_MINUTE);
        }
    }
}
