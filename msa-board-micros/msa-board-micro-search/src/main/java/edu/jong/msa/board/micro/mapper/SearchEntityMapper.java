package edu.jong.msa.board.micro.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

import edu.jong.msa.board.client.response.MemberInfo;
import edu.jong.msa.board.common.type.SortEnum.MemberSort;
import edu.jong.msa.board.common.type.SortEnum.Order;
import edu.jong.msa.board.domain.entity.MemberEntity;
import edu.jong.msa.board.domain.entity.QMemberEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SearchEntityMapper {

	public static final QMemberEntity TB_MEMBER = QMemberEntity.memberEntity;

	default <T extends Comparable<?>> ComparableExpressionBase<?> toColumn(MemberSort sort) {

		if (sort == null) {
			return TB_MEMBER.createdDateTime;
		} else {
			switch (sort) {
				case USERNAME:		return TB_MEMBER.username;
				case NAME:			return TB_MEMBER.name;
				case EMAIL:			return TB_MEMBER.email;
				case CREATED_DATE:	return TB_MEMBER.createdDateTime;
				case UPDATED_DATE:	return TB_MEMBER.updatedDateTime;
				default:			return TB_MEMBER.createdDateTime;
			}
		}
	}

	default <T extends Comparable<?>> OrderSpecifier<?> toOrderCondition(
			ComparableExpressionBase<?> column, Order order) {
		
		if (order == null) {
			return column.asc();
		} else {
			switch (order) {
				case DESC: 	return column.desc();
				case ASC: 	return column.asc();
				default: 	return column.asc();	
			}
		}
	}

	MemberInfo toInfo(MemberEntity entity);
	
}
