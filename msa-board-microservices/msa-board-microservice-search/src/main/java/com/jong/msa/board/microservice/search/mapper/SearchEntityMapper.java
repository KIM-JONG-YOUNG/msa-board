package com.jong.msa.board.microservice.search.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.jong.msa.board.client.search.response.MemberListResponse;
import com.jong.msa.board.client.search.response.PostListResponse;
import com.jong.msa.board.domain.member.entity.MemberEntity;
import com.jong.msa.board.domain.post.entity.PostEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SearchEntityMapper {

	MemberListResponse.Info toListInfo(MemberEntity entity);

	PostListResponse.Writer toWriter(MemberEntity member);

	@Mapping(target = "id", source = "post.id")
	@Mapping(target = "writer", source = "member")
	@Mapping(target = "createdDateTime", source = "post.createdDateTime")
	@Mapping(target = "updatedDateTime", source = "post.updatedDateTime")
	@Mapping(target = "state", source = "post.state")
	PostListResponse.Info toListInfo(PostEntity post, MemberEntity member);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	MemberEntity updateEntity(MemberEntity entity, @MappingTarget MemberEntity targetEntity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	PostEntity updateEntity(PostEntity entity, @MappingTarget PostEntity targetEntity);

}
