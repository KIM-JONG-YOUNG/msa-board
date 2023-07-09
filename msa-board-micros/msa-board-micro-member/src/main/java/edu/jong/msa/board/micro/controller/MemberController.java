package edu.jong.msa.board.micro.controller;

import java.util.UUID;

import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import edu.jong.msa.board.client.operations.MemberOperations;
import edu.jong.msa.board.client.request.MemberParam;
import edu.jong.msa.board.client.response.MemberDetails;
import edu.jong.msa.board.common.BoardConstants.URLPaths;
import edu.jong.msa.board.micro.service.MemberService;
import edu.jong.msa.board.micro.validator.MemberParamValidator;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberOperations {

	private final MemberParamValidator validator;
	
	private final MemberService service;
	
	@Override
	public ResponseEntity<Void> createMember(MemberParam param) {

		validator.validate(param, MemberParamValidator.CreateValidGroup.class);
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.header(HttpHeaders.LOCATION, new StringBuffer()
						.append(URLPaths.MEMBER_URL)
						.append("/")
						.append(service.createMember(param))
						.toString())
				.build();
	}

	@Override
	public ResponseEntity<Void> modifyMember(UUID id, MemberParam param) {

		validator.validate(param, MemberParamValidator.ModifyValidGroup.class);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.header(HttpHeaders.LOCATION, new StringBuffer()
						.append(URLPaths.MEMBER_URL)
						.append("/")
						.append(service.modifyMember(id, param))
						.toString())
				.build();
	}

	@Override
	public ResponseEntity<MemberDetails> getMember(UUID id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(service.getMember(id));
	}

	@Override
	public ResponseEntity<MemberDetails> loginMember(MemberParam param) {
		
		validator.validate(param, MemberParamValidator.LoginValidGroup.class);
		
		return ResponseEntity.status(HttpStatus.OK)
				.body(service.loginMember(param));
	}

}
