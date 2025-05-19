package com.project.beauty_care.domain.board.service;

import com.project.beauty_care.domain.attachFile.AttachFileConverter;
import com.project.beauty_care.domain.attachFile.dto.AttachFileResponse;
import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.board.BoardConverter;
import com.project.beauty_care.domain.board.dto.AdminBoardResponse;
import com.project.beauty_care.domain.board.dto.BoardCriteria;
import com.project.beauty_care.domain.board.repository.BoardRepository;
import com.project.beauty_care.domain.code.CodeConverter;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import com.project.beauty_care.domain.mapper.BoardMapper;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.MemberConverter;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.global.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBoardService {
    private final BoardRepository repository;
    private final FileUtils fileUtils;
    private final AttachFileConverter fileConverter;
    private final BoardConverter converter;
    private final CodeConverter codeConverter;
    private final MemberConverter memberConverter;
    private final MemberService memberService;

    // 신고 게시물 보기
    public Page<AdminBoardResponse> findReportedBoards(Pageable pageable, BoardCriteria criteria) {
        Page<Board> pageResults =
                repository.findAllByCriteriaAndBoardReportsIsNotEmpty(criteria, pageable);

        List<AdminBoardResponse> contents = pageResults.stream()
                .map(board -> {
                    int reportCount = board.getBoardReports().size();

                    CodeResponse grade = codeConverter.toResponse(board.getGrade());
                    List<AttachFileResponse> fileList = getFileResponseListFromBoard(board);

                    Member createMember = memberService.findMemberById(board.getCreatedBy());

                    return converter.toResponse(
                            board, fileList, grade, reportCount, memberConverter.toResponse(createMember)
                    );
                })
                .toList();

        return new PageImpl<>(contents, pageable, pageResults.getTotalElements());
    }

    // hard-delete
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    // 전체 게시물 조회
    public Page<AdminBoardResponse> findAllBoards(Pageable pageable) {
        return null;
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
