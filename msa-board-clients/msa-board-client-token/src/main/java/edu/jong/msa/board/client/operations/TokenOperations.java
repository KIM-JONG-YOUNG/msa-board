package edu.jong.msa.board.client.operations;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import edu.jong.msa.board.client.response.SessionTokens;
import edu.jong.msa.board.client.response.TokenInfo;
import edu.jong.msa.board.common.BoardConstants.HeaderNames;
import edu.jong.msa.board.common.BoardConstants.ServiceNames;
import edu.jong.msa.board.common.BoardConstants.URLPaths;

@FeignClient(ServiceNames.TOKEN_SERVICE)
public interface TokenOperations {

	@PostMapping(value = URLPaths.TOKEN_URL + "/{memberId}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SessionTokens> generateToken(
			@PathVariable UUID memberId);

	@DeleteMapping(value = URLPaths.TOKEN_URL)
	ResponseEntity<Void> disableToken(
			@RequestHeader(name = HeaderNames.ACCESS_TOKEN, required = false) String accessToken,
			@RequestHeader(name = HeaderNames.REFRESH_TOKEN, required = false) String refreshToken);

	@PostMapping(value = URLPaths.TOKEN_URL + "/refresh",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SessionTokens> refreshToken(
			@RequestHeader(name = HeaderNames.REFRESH_TOKEN) String refreshToken);

	@GetMapping(value = URLPaths.TOKEN_URL,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<TokenInfo> getInfoByToken(
			@RequestHeader(name = HeaderNames.ACCESS_TOKEN) String accessToken);

}
