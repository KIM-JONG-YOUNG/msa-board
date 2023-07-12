package edu.jong.msa.board.micro.controller;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import edu.jong.msa.board.client.operations.TokenOperations;
import edu.jong.msa.board.client.response.SessionTokens;
import edu.jong.msa.board.client.response.TokenInfo;
import edu.jong.msa.board.micro.service.TokenService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenController implements TokenOperations {

	private final TokenService service;
	
	@Override
	public ResponseEntity<SessionTokens> generateSessionTokens(UUID memberId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(service.generateSessionTokens(memberId));
	}

	@Override
	public ResponseEntity<Void> disableSessionTokens(UUID memberId, String accessToken, String refreshToken) {

		if (memberId != null) {
			service.disableSessionTokens(memberId);
		}
		
		if (StringUtils.isNotBlank(accessToken)) {
			service.disableAccessToken(accessToken);
		}

		if (StringUtils.isNotBlank(refreshToken)) {
			service.disableRefreshToken(refreshToken);
		}
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Override
	public ResponseEntity<SessionTokens> refreshSessionTokens(String refreshToken) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(service.refreshSessionTokens(refreshToken));
	}

	@Override
	public ResponseEntity<TokenInfo> getInfoByAccessToken(String accessToken) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(service.getInfoByAccessToken(accessToken));
	}

}
