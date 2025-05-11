package com.project.beauty_care.domain.board.service;

import com.project.beauty_care.domain.attachFile.AttachFile;
import com.project.beauty_care.domain.attachFile.AttachFileConverter;
import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.domain.attachFile.dto.AttachFileCreateRequest;
import com.project.beauty_care.domain.attachFile.service.AttachFileService;
import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.board.BoardConverter;
import com.project.beauty_care.domain.board.repository.BoardRepository;
import com.project.beauty_care.domain.board.BoardValidator;
import com.project.beauty_care.domain.board.dto.BoardCreateRequest;
import com.project.beauty_care.domain.board.dto.BoardCriteria;
import com.project.beauty_care.domain.board.dto.BoardResponse;
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

        List<String> fileFullPathList = new ArrayList<>();

        // build file request
        if (!request.getAttachFiles().isEmpty()) {
            AttachFileCreateRequest fileCreateRequest
                    = fileConverter.buildRequest(MappedEntity.BOARD, String.valueOf(entityId), request.getAttachFiles());

            List<AttachFile> attachFileList = fileService.uploadFile(fileCreateRequest);

            fileFullPathList = attachFileList.stream()
                    .map(fileConverter::extractFileFullPath)
                    .toList();

            savedEntity.getAttachFiles().addAll(attachFileList);
        }

        return converter.toResponse(savedEntity, fileFullPathList, codeConverter.toResponse(grade));
    }

    @Transactional(readOnly = true)
    public BoardResponse findBoardById(Long boardId) {
        Board board = findById(boardId);

        CodeResponse grade = codeConverter.toResponse(board.getGrade());

        List<String> fileFullPathList = board.getAttachFiles().stream()
                .map(fileConverter::extractFileFullPath)
                .toList();

        return converter.toResponse(board, fileFullPathList, grade);
    }

    public Page<BoardResponse> findBoardAllPageByCriteria(BoardCriteria criteria, Pageable pageable) {
        Page<Board> result = repository.findAllByCriteriaPage(criteria, pageable);

        List<BoardResponse> contentList = result.stream()
                .map(board -> {
                    CodeResponse grade = codeConverter.toResponse(board.getGrade());

                    List<String> fileFullPathList = board.getAttachFiles().stream()
                            .map(fileConverter::extractFileFullPath)
                            .toList();

                    return converter.toResponse(board, fileFullPathList, grade);
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
