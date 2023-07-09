package edu.jong.msa.board.client.operations;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import edu.jong.msa.board.client.request.MemberCondition;
import edu.jong.msa.board.client.response.MemberInfo;
import edu.jong.msa.board.client.response.PagingList;
import edu.jong.msa.board.common.BoardConstants.ServiceNames;
import edu.jong.msa.board.common.BoardConstants.URLPaths;

@FeignClient(ServiceNames.SEARCH_SERVICE)
public interface SearchOperations {

	@GetMapping(value = URLPaths.MEMBER_URL + URLPaths.SEARCH_URL,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PagingList<MemberInfo>> searchMemberList(
			MemberCondition cond);
	
}
