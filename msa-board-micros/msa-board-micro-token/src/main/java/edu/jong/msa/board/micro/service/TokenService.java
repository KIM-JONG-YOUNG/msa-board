package edu.jong.msa.board.micro.service;

import java.util.UUID;

import edu.jong.msa.board.client.response.SessionTokens;
import edu.jong.msa.board.client.response.TokenInfo;

public interface TokenService {

	SessionTokens generateSessionTokens(UUID memberId);

	SessionTokens refreshSessionTokens(String refreshToken);

	void disableSessionTokens(UUID memberId);

	void disableAccessToken(String accessToken);

	void disableRefreshToken(String refreshToken);

	TokenInfo getInfoByAccessToken(String accessToken);
	
}
