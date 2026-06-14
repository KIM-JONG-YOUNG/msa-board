package com.jong.msaboard.microservice.member.mapper;

import com.jong.msaboard.microservice.member.entity.MemberEntity;
import com.jong.msaboard.microservice.member.request.MemberJoinRequest;
import com.jong.msaboard.microservice.member.request.MemberModifyRequest;
import com.jong.msaboard.microservice.member.request.MemberPasswordModifyRequest;
import com.jong.msaboard.microservice.member.request.admin.MemberGroupModifyAdminRequest;
import com.jong.msaboard.microservice.member.request.admin.MemberStatusModifyAdminRequest;
import com.jong.msaboard.microservice.member.response.MemberDetailResponse;
import com.jong.msaboard.microservice.member.response.admin.MemberDetailAdminResponse;
import com.jong.msaboard.microservice.member.response.internal.MemberDetailInternalResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MemberEntityMapper {

    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "group", constant = "USER")
    @Mapping(target = "status", constant = "ACTIVE")
    MemberEntity toEntity(MemberJoinRequest request, @Context PasswordEncoder passwordEncoder);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MemberEntity updateEntity(MemberModifyRequest request, @MappingTarget MemberEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "newPassword", qualifiedByName = "encodePassword")
    MemberEntity updateEntity(MemberPasswordModifyRequest request, @MappingTarget MemberEntity entity, @Context PasswordEncoder passwordEncoder);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MemberEntity updateEntity(MemberGroupModifyAdminRequest request, @MappingTarget MemberEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MemberEntity updateEntity(MemberStatusModifyAdminRequest request, @MappingTarget MemberEntity entity);

    MemberDetailResponse toDetailResponse(MemberEntity entity);

    MemberDetailAdminResponse toDetailAdminResponse(MemberEntity entity);

    MemberDetailInternalResponse toDetailInternalResponse(MemberEntity entity);

    @Named("encodePassword")
    default String encode(String password, @Context PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(password);
    }

}
