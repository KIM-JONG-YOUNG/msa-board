package com.jong.msa.board.microservice.search.mapper;

import com.jong.msa.board.microservice.search.feign.CommentFeignClient;
import com.jong.msa.board.microservice.search.feign.MemberFeignClient;
import com.jong.msa.board.microservice.search.feign.PostFeignClient;
import com.jong.msa.board.microservice.search.response.CommentListResponse;
import com.jong.msa.board.support.domain.document.CommentDocument;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentDocumentMapper {

    CommentDocument toDocument(CommentFeignClient.GetResponse source);

    CommentDocument.Writer toDocumentWriter(MemberFeignClient.GetResponse source);

    CommentDocument.Post toDocumentPost(PostFeignClient.GetResponse source);

    CommentDocument.Parent toDocumentParent(CommentFeignClient.GetResponse source);

    CommentListResponse.Item toListItem(CommentDocument source);

    CommentListResponse.Writer toListWriter(CommentDocument.Writer source);

    CommentListResponse.Post toListPost(CommentDocument.Post source);

    CommentListResponse.Parent toListParent(CommentDocument.Parent source);

}
