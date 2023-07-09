package edu.jong.msa.board.micro.service;

import edu.jong.msa.board.client.request.MemberCondition;
import edu.jong.msa.board.client.response.MemberInfo;
import edu.jong.msa.board.client.response.PagingList;

public interface SearchService {

	PagingList<MemberInfo> searchMemberList(MemberCondition cond);
	
}
