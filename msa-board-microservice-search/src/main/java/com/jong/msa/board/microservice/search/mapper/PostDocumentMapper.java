package com.jong.msa.board.microservice.search.mapper;

import com.jong.msa.board.microservice.search.feign.MemberFeignClient;
import com.jong.msa.board.microservice.search.feign.PostFeignClient;
import com.jong.msa.board.microservice.search.response.PostListResponse;
import com.jong.msa.board.support.domain.document.PostDocument;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostDocumentMapper {

    PostDocument toDocument(PostFeignClient.GetResponse source);

    PostDocument.Writer toDocumentWriter(MemberFeignClient.GetResponse source);

    PostListResponse.Item toListItem(PostDocument source);

    PostListResponse.Writer toListWriter(PostDocument.Writer source);

}
