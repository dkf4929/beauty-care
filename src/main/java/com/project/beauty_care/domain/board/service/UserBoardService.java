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
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.EntityNotFoundException;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBoardService {
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

        // 사용자의 이전 게시물 생성 시간 체크
        validator.validCreatedDateTime(user.getRole().getRoleName(), user.getMemberId());

        // 게시물 유형 validation 체크
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

    // 내가 작성한 게시물 조회
    @Transactional(readOnly = true)
    public Page<BoardResponse> findBoardsByLoginUserAndIsUse(AppUser loginUser, Boolean isUse, Pageable pageable) {
        Page<Board> pageResult = repository.findByCreatedByAndIsUse(loginUser.getMemberId(), isUse, pageable);

        List<BoardResponse> contents = pageResult.getContent().stream()
                .map(board -> {
                    CodeResponse grade = codeConverter.toResponse(board.getGrade());

                    List<AttachFileResponse> fileList = getFileResponseListFromBoard(board);

                    return converter.toResponse(board, fileList, grade);
                })
                .toList();

        return new PageImpl<>(contents, pageable, pageResult.getTotalElements());
    }

    @Transactional
    public BoardResponse findBoardByIdAndConvertResponse(Long boardId, AppUser loginUser) {
        Board entity = findById(boardId);

        CodeResponse grade = codeConverter.toResponse(entity.getGrade());

        List<AttachFileResponse> fileList = getFileResponseListFromBoard(entity);

        int readCount = boardReadService.getReadCountAndSaveRedis(boardId,
                loginUser.getMemberId(), entity.getReadCount());

        // 조회 수 업데이트
        entity.updateReadCount(readCount);

        return converter.toResponse(entity, fileList, grade);
    }

    public Board findBoardById(Long boardId) {
        return findById(boardId);
    }

    public Page<BoardResponse> findBoardAllPageByCriteria(BoardCriteria criteria, Pageable pageable) {
        Page<Board> result = repository.findAllByCriteriaPage(criteria, pageable);

        List<BoardResponse> contentList = result.stream()
                .map(board -> {
                    CodeResponse grade = codeConverter.toResponse(board.getGrade());

                    List<AttachFileResponse> fileList = getFileResponseListFromBoard(board);

                    return converter.toResponse(board, fileList, grade);
                })
                .toList();

        return new PageImpl<>(contentList, pageable, result.getTotalElements());
    }

    private Board findById(Long boardId) {
        return repository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(Errors.NOT_FOUND_BOARD));
    }

    private List<AttachFileResponse> getFileResponseListFromBoard(Board board) {
        return board.getAttachFiles().stream()
                .map(file -> {
                    String fileFullPath = fileUtils.extractFileFullPath(file);

                    return fileConverter.toResponse(file, fileFullPath);
                })
                .toList();
    }
}
