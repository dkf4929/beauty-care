package com.project.beauty_care.domain.board;

import com.project.beauty_care.domain.attachFile.dto.AttachFileResponse;
import com.project.beauty_care.domain.board.dto.AdminBoardResponse;
import com.project.beauty_care.domain.board.dto.BoardCreateRequest;
import com.project.beauty_care.domain.board.dto.BoardResponse;
import com.project.beauty_care.domain.code.Code;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.mapper.BoardMapper;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardConverter {
    public Board buildEntity(BoardCreateRequest request, Code grade) {
        return Board.builder()
                .boardType(request.getBoardType())
                .grade(grade)
                .title(request.getTitle())
                .content(request.getContent())
                .isUse(request.getIsUse())
                .build();
    }

    public BoardResponse toResponse(Board board,
                                    List<AttachFileResponse> attachFiles,
                                    CodeResponse grade) {
        return BoardMapper.INSTANCE.toResponse(board, attachFiles, grade);
    }

    public AdminBoardResponse toResponse(Board board,
                                         List<AttachFileResponse> attachFiles,
                                         CodeResponse grade,
                                         Integer reportCount,
                                         MemberResponse createMember) {
        return BoardMapper.INSTANCE.toResponse(board, attachFiles, grade, reportCount, createMember);
    }
}
