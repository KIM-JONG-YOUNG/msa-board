package edu.jong.msa.board.client.operations;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import edu.jong.msa.board.client.request.MemberParam;
import edu.jong.msa.board.client.response.MemberDetails;
import edu.jong.msa.board.common.BoardConstants.ServiceNames;
import edu.jong.msa.board.common.BoardConstants.URLPaths;

@FeignClient(ServiceNames.MEMBER_SERVICE)
public interface MemberOperations {

	@PostMapping(value = URLPaths.MEMBER_URL,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> createMember(
			@RequestBody MemberParam param);

	@PutMapping(value = URLPaths.MEMBER_URL + "/{id}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyMember(
			@PathVariable UUID id,
			@RequestBody MemberParam param);

	@GetMapping(value = URLPaths.MEMBER_URL + "/{id}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MemberDetails> getMember(
			@PathVariable UUID id);

	@PostMapping(value = URLPaths.MEMBER_URL + "/login",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MemberDetails> loginMember(
			@RequestBody MemberParam param);

}
