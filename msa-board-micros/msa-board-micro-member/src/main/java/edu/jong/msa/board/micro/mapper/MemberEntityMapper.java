package edu.jong.msa.board.micro.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.jong.msa.board.client.request.MemberParam;
import edu.jong.msa.board.client.response.MemberDetails;
import edu.jong.msa.board.domain.entity.MemberEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberEntityMapper {

	MemberEntity toEntity(MemberParam param);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "password", expression = "java(encoder.encode(entity.getPassword()))")
	MemberEntity encodeEntity(PasswordEncoder encoder, @MappingTarget MemberEntity entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	MemberEntity updateEntity(MemberParam param, @MappingTarget MemberEntity entity);

	MemberDetails toDetails(MemberEntity entity);
	
}
