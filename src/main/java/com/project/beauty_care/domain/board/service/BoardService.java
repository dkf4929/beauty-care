package com.project.beauty_care.domain.board.service;

import com.project.beauty_care.domain.attachFile.AttachFile;
import com.project.beauty_care.domain.attachFile.AttachFileConverter;
import com.project.beauty_care.domain.attachFile.MappedEntity;
import com.project.beauty_care.domain.attachFile.dto.AttachFileCreateRequest;
import com.project.beauty_care.domain.attachFile.service.AttachFileService;
import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.board.BoardConverter;
import com.project.beauty_care.domain.board.BoardRepository;
import com.project.beauty_care.domain.board.BoardValidation;
import com.project.beauty_care.domain.board.dto.BoardCreateRequest;
import com.project.beauty_care.domain.board.dto.BoardResponse;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.CodeConverter;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.code.service.CodeService;
import com.project.beauty_care.domain.enums.BoardType;
import com.project.beauty_care.global.security.dto.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository repository;

    private final BoardConverter converter;
    private final AttachFileConverter fileConverter;
    private final CodeConverter codeConverter;

    private final BoardValidation validator;

    private final AttachFileService fileService;
    private final CodeService codeService;

    @Transactional
    public BoardResponse createBoard(BoardCreateRequest request, AppUser user) {
        BoardType boardType = request.getBoardType();
        String role = user.getRole().getRoleName();

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
}
