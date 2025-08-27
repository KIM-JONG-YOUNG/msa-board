package com.jong.msa.board.microservice.post.mapper;

import com.jong.msa.board.microservice.post.request.PostCreateRequest;
import com.jong.msa.board.microservice.post.request.PostModifyRequest;
import com.jong.msa.board.microservice.post.response.PostDetailsResponse;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import com.jong.msa.board.support.domain.entity.PostEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostEntityMapper {

    PostEntity toEntity(PostCreateRequest source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostEntity updateEntity(@MappingTarget PostEntity target, PostModifyRequest source);

    PostDetailsResponse toDetail(PostEntity source);

    PostDetailsResponse.Writer toDetailWriter(MemberEntity source);

}
