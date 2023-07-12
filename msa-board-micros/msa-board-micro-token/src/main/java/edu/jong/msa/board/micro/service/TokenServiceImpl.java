package edu.jong.msa.board.micro.service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import edu.jong.msa.board.client.operations.MemberOperations;
import edu.jong.msa.board.client.response.MemberDetails;
import edu.jong.msa.board.client.response.SessionTokens;
import edu.jong.msa.board.client.response.TokenInfo;
import edu.jong.msa.board.common.BoardConstants.CachingKeys;
import edu.jong.msa.board.common.type.DBCodeEnum.State;
import edu.jong.msa.board.micro.TokenApplication;
import edu.jong.msa.board.micro.exception.ExpiredTokenException;
import edu.jong.msa.board.micro.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

	private final RedisTemplate<String, String> redisTemplate;

	private final TokenApplication.Properties properties;
	
	private final MemberOperations memberOperations;
	
	private String generateToken(UUID memberId, String secretKey, long expireSeconds) {
		
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + (expireSeconds * 1000L));

		return Jwts.builder()
				.setSubject(memberId.toString())
				.setIssuedAt(now)
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
	}

	private UUID getMemberIdFromToken(String token, String secretKey) {
		try {
			return UUID.fromString(Jwts.parser()
					.setSigningKey(secretKey)
		        	.parseClaimsJws(token)
		        	.getBody()
		        	.getSubject());
		} catch (ExpiredJwtException e) {
			throw new ExpiredTokenException("만료된 토큰입니다.");
		} catch (Exception e) {
			throw new InvalidTokenException("유효하지 않은 토큰입니다.");
		}
	}
	
	private void disableToken(String token, long expireSeconds) {
		String cachingKey = CachingKeys.BLACKLIST_TOKEN_KEY + token;
		redisTemplate.opsForValue().set(cachingKey, "disable-token",
				expireSeconds, TimeUnit.SECONDS);
	}
	
	private String generateAccessToken(UUID memberId) {
		
		String accessToken = generateToken(memberId, 
				properties.getAccessSecretKey(), 
				properties.getAccessExpireSeconds());
		
		String cachingKey = new StringBuffer()
				.append(CachingKeys.ACCESS_TOKEN_KEY)
				.append(accessToken).append("::")
				.append(memberId).toString(); 
		
		redisTemplate.opsForValue().set(cachingKey, "access-token",
				properties.getAccessExpireSeconds(), TimeUnit.SECONDS);
		
		return accessToken;
	}

	private String generateRefreshToken(UUID memberId) {
		
		String refreshToken = generateToken(memberId, 
				properties.getRefreshSecretKey(), 
				properties.getRefreshExpireSeconds());
		
		String cachingKey = new StringBuffer()
				.append(CachingKeys.REFRESH_TOKEN_KEY)
				.append(refreshToken).append("::")
				.append(memberId).toString(); 
		
		redisTemplate.opsForValue().set(cachingKey, "refresh-token",
				properties.getRefreshExpireSeconds(), TimeUnit.SECONDS);
		
		return refreshToken;
	}

	private UUID getMemberIdByAccessToken(String accessToken) {
		
		if (redisTemplate.hasKey(CachingKeys.BLACKLIST_TOKEN_KEY + accessToken)) {
			throw new InvalidTokenException("로그아웃된 Access Token 입니다.");
		} else {
			try {
				return getMemberIdFromToken(accessToken, properties.getAccessSecretKey());
			} catch (ExpiredTokenException e) {
				throw new ExpiredTokenException("만료된 Access Token 입니다.");
			} catch (InvalidTokenException e) {
				throw new InvalidTokenException("유효하지 않은 Access Token 입니다.");
			}
		}
	}

	private UUID getMemberIdByRefreshToken(String refreshToken) {
		
		if (redisTemplate.hasKey(CachingKeys.BLACKLIST_TOKEN_KEY + refreshToken)) {
			throw new InvalidTokenException("비활성화된 Refresh Token 입니다.");
		} else {
			try {
				return getMemberIdFromToken(refreshToken, properties.getRefreshSecretKey());
			} catch (ExpiredTokenException e) {
				throw new ExpiredTokenException("만료된 Refresh Token 입니다.");
			} catch (InvalidTokenException e) {
				throw new InvalidTokenException("유효하지 않은 Refresh Token 입니다.");
			}
		}
	}

	@Override
	public SessionTokens generateSessionTokens(UUID memberId) {
		return SessionTokens.builder()
				.accessToken(generateAccessToken(memberId))
				.refreshToken(generateRefreshToken(memberId))
				.build();
	}

	@Override
	public SessionTokens refreshSessionTokens(String refreshToken) {

		UUID memberId = getMemberIdByRefreshToken(refreshToken);
		
		SessionTokens sessionTokens = generateSessionTokens(memberId);

		disableRefreshToken(refreshToken);;
		
		return sessionTokens;
	}

	@Override
	public TokenInfo getInfoByAccessToken(String accessToken) {
		
		UUID memberId = getMemberIdByAccessToken(accessToken);

		MemberDetails member = memberOperations.getMember(memberId).getBody();
		
		if (member.getState() == State.ACTIVE) {
			return TokenInfo.builder()
					.memberId(member.getId())
					.memberGroup(member.getGroup())
					.build();
		} else {
			throw new InvalidTokenException("비활성화된 계정의 Access Token 입니다.");
		}
	}

	@Override
	public void disableSessionTokens(UUID memberId) {
		
		redisTemplate.keys(CachingKeys.ACCESS_TOKEN_KEY + "*::" + memberId).stream()
			.map(k -> k.split("::")[1]).forEach(k -> disableAccessToken(k));
		
		redisTemplate.keys(CachingKeys.REFRESH_TOKEN_KEY + "*::" + memberId).stream()
			.map(k -> k.split("::")[1]).forEach(k -> disableRefreshToken(k));
	}

	@Override
	public void disableAccessToken(String accessToken) {
		disableToken(accessToken, properties.getAccessExpireSeconds());
	}

	@Override
	public void disableRefreshToken(String refreshToken) {
		disableToken(refreshToken, properties.getRefreshExpireSeconds());
	}

}
