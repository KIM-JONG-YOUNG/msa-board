package edu.jong.msa.board.micro.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import edu.jong.msa.board.client.request.MemberCondition;
import edu.jong.msa.board.client.response.MemberInfo;
import edu.jong.msa.board.client.response.PagingList;
import edu.jong.msa.board.domain.entity.QMemberEntity;
import edu.jong.msa.board.domain.utils.QueryExpressionUtils;
import edu.jong.msa.board.micro.mapper.SearchEntityMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

	private final SearchEntityMapper mapper;

	private final JPAQueryFactory queryFactory;
	
	private final QMemberEntity TB_MEMBER = QMemberEntity.memberEntity;
	
	@Transactional(readOnly = true)
	@Override
	public PagingList<MemberInfo> searchMemberList(MemberCondition cond) {

		BooleanExpression[] searchCondition = new BooleanExpression[] {
				QueryExpressionUtils.contains(TB_MEMBER.username, cond.getUsername()),
				QueryExpressionUtils.contains(TB_MEMBER.name, cond.getName()),
				QueryExpressionUtils.equals(TB_MEMBER.gender, cond.getGender()),
				QueryExpressionUtils.contains(TB_MEMBER.email, cond.getEmail()),
				QueryExpressionUtils.equals(TB_MEMBER.group, cond.getGroup()),
				QueryExpressionUtils.equals(TB_MEMBER.state, cond.getState()),
				QueryExpressionUtils.betweenDate(TB_MEMBER.createdDateTime, 
						cond.getFrom(), cond.getTo()),
		};
		
		OrderSpecifier<?> orderCondition = mapper.toOrderCondition(
				mapper.toColumn(cond.getSort()), cond.getOrder());

		long totalCount = queryFactory
				.select(TB_MEMBER.count())
				.from(TB_MEMBER)
				.where(searchCondition)
				.fetchOne();
		
		List<MemberInfo> list = queryFactory
				.select(TB_MEMBER)
				.from(TB_MEMBER)
				.where(searchCondition)
				.orderBy(orderCondition)
				.fetch().stream()
				.map(x -> mapper.toInfo(x))
				.collect(Collectors.toList());
		
		return new PagingList<>(list, cond, totalCount);
	}

}
