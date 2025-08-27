package com.jong.msa.board.microservice.search.mapper;

import com.jong.msa.board.microservice.search.feign.MemberFeignClient;
import com.jong.msa.board.microservice.search.response.MemberListResponse;
import com.jong.msa.board.support.domain.document.MemberDocument;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberDocumentMapper {

    MemberDocument toDocument(MemberFeignClient.GetResponse source);

    MemberListResponse.Item toListItem(MemberDocument source);

}
