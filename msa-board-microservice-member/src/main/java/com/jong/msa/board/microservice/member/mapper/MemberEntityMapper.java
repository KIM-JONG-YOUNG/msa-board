package com.jong.msa.board.microservice.member.mapper;

import com.jong.msa.board.microservice.member.request.MemberCreateRequest;
import com.jong.msa.board.microservice.member.request.MemberModifyRequest;
import com.jong.msa.board.microservice.member.response.MemberDetailsResponse;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberEntityMapper {

    MemberEntity toEntity(MemberCreateRequest source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MemberEntity updateEntity(@MappingTarget MemberEntity entity, MemberModifyRequest source);

    MemberDetailsResponse toDetails(MemberEntity source);

}
