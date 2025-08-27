package com.jong.msa.board.microservice.comment.mapper;

import com.jong.msa.board.microservice.comment.request.CommentCreateRequest;
import com.jong.msa.board.microservice.comment.response.CommentDetailsResponse;
import com.jong.msa.board.microservice.comment.response.CommentTreeResponse;
import com.jong.msa.board.support.domain.entity.CommentEntity;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEntityMapper {

    CommentEntity toEntity(CommentCreateRequest source);

    CommentDetailsResponse toDetail(CommentEntity source);

    CommentDetailsResponse.Writer toDetailWriter(MemberEntity source);

    @Mapping(target = "children", expression = "java(new java.util.ArrayList<>())")
    CommentTreeResponse.Item toTreeItem(CommentEntity source);

}
