package edu.jong.msa.board.micro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import edu.jong.msa.board.client.operations.SearchOperations;
import edu.jong.msa.board.client.request.MemberCondition;
import edu.jong.msa.board.client.response.MemberInfo;
import edu.jong.msa.board.client.response.PagingList;
import edu.jong.msa.board.micro.service.SearchService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SearchController implements SearchOperations {

	private final SearchService service;
	
	@Override
	public ResponseEntity<PagingList<MemberInfo>> searchMemberList(MemberCondition cond) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(service.searchMemberList(cond));
	}

}
