package com.project.beauty_care.domain.mapper;

import com.project.beauty_care.domain.attachFile.dto.AttachFileResponse;
import com.project.beauty_care.domain.board.Board;
import com.project.beauty_care.domain.board.dto.BoardResponse;
import com.project.beauty_care.domain.code.dto.CodeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    BoardMapper INSTANCE = Mappers.getMapper(BoardMapper.class);

    @Mapping(target = "id", source = "board.id")
    @Mapping(target = "boardType", source = "board.boardType")
    @Mapping(target = "grade", source = "grade")
    @Mapping(target = "title", source = "board.title")
    @Mapping(target = "content", source = "board.content")
    @Mapping(target = "attachFiles", source = "attachFiles")
    @Mapping(target = "readCount", source = "board.readCount")
    @Mapping(target = "isUse", source = "board.isUse")
    @Mapping(target = "createdBy", source = "board.createdBy")
    @Mapping(target = "updatedBy", source = "board.updatedBy")
    @Mapping(target = "createdDateTime", source = "board.createdDateTime")
    @Mapping(target = "updatedDateTime", source = "board.updatedDateTime")
    BoardResponse toResponse(Board board, List<AttachFileResponse> attachFiles, CodeResponse grade);
}
