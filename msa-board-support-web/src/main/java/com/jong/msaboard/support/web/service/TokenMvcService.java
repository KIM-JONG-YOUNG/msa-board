package com.jong.msaboard.support.web.service;

import com.jong.msaboard.common.constants.RedisKeyPrefixes;
import com.jong.msaboard.common.type.Group;
import com.jong.msaboard.support.web.condition.ConditionalOnWebMvcSecurity;
import com.jong.msaboard.support.web.error.SecurityErrorCode;
import com.jong.msaboard.support.web.exception.RevokedJwtException;
import com.jong.msaboard.support.web.properties.TokenProperties;
import com.jong.msaboard.support.web.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnWebMvcSecurity
public class TokenMvcService {

    private final TokenProperties tokenProperties;

    private final RedisTemplate<String, String> redisTemplate;

    public String generateAccessToken(UUID memberId, Group memberGroup) {
        var accessToken = TokenUtils.generateToken(memberId, memberGroup,
            tokenProperties.accessToken().secretKey(),
            tokenProperties.accessToken().validDuration());
        addWhitelistToken(accessToken, tokenProperties.accessToken().secretKey());
        return accessToken;
    }

    public String generateRefreshToken(UUID memberId) {
        var refreshToken = TokenUtils.generateToken(memberId, null,
            tokenProperties.refreshToken().secretKey(),
            tokenProperties.refreshToken().validDuration());
        addWhitelistToken(refreshToken, tokenProperties.refreshToken().secretKey());
        return refreshToken;
    }

    public Authentication getAuthenticationFromAccessToken(String accessToken) {
        try {
            var memberId = TokenUtils.getMemberId(accessToken, tokenProperties.accessToken().secretKey());
            var memberGroup = TokenUtils.getMemberGroup(accessToken, tokenProperties.accessToken().secretKey());
            if (existsWhitelistToken(accessToken, tokenProperties.accessToken().secretKey())) {
                var authorities = Set.of(new SimpleGrantedAuthority("ROLE_" + memberGroup.name()));
                return new UsernamePasswordAuthenticationToken(memberId, null, authorities);
            }
            throw new RevokedJwtException("취소된 Access Token 입니다.");
        } catch (RevokedJwtException e) {
            throw SecurityErrorCode.REVOKED_ACCESS_TOKEN.toException();
        } catch (ExpiredJwtException e) {
            throw SecurityErrorCode.EXPIRED_ACCESS_TOKEN.toException();
        } catch (Exception e) {
            throw SecurityErrorCode.INVALID_ACCESS_TOKEN.toException();
        }
    }

    public UUID getMemberIdFromRefreshToken(String refreshToken) {
        try {
            var memberId = TokenUtils.getMemberId(refreshToken, tokenProperties.refreshToken().secretKey());
            if (existsWhitelistToken(refreshToken, tokenProperties.refreshToken().secretKey())) {
                return memberId;
            }
            throw new RevokedJwtException("취소된 Refresh Token 입니다.");
        } catch (RevokedJwtException e) {
            throw SecurityErrorCode.REVOKED_REFRESH_TOKEN.toException();
        } catch (ExpiredJwtException e) {
            throw SecurityErrorCode.EXPIRED_REFRESH_TOKEN.toException();
        } catch (Exception e) {
            throw SecurityErrorCode.INVALID_REFRESH_TOKEN.toException();
        }
    }

    public void revokeAccessToken(String accessToken) {
        try {
            removeWhitelistToken(accessToken, tokenProperties.accessToken().secretKey());
        } catch (ExpiredJwtException e) {
            log.warn("이미 만료된 Access Token 입니다.");
        } catch (Exception e) {
            log.warn("유효하지 않은 Access Token 입니다.");
        }
    }

    public void revokeRefreshToken(String refreshToken) {
        try {
            removeWhitelistToken(refreshToken, tokenProperties.refreshToken().secretKey());
        } catch (ExpiredJwtException e) {
            log.warn("이미 만료된 Refresh Token 입니다.");
        } catch (Exception e) {
            log.warn("유효하지 않은 Refresh Token 입니다.");
        }
    }

    private void addWhitelistToken(String token, SecretKey secretKey) {
        var memberId = TokenUtils.getMemberId(token, secretKey);
        var expiration = TokenUtils.getExpiration(token, secretKey);
        var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
        var whitelistTokenCacheDuration = Duration.ofMillis(Math.max(
            redisTemplate.getExpire(whitelistTokenCacheKey, TimeUnit.MILLISECONDS),
            Duration.between(LocalDateTime.now(), expiration).toMillis()
        ));
        redisTemplate.opsForSet().add(whitelistTokenCacheKey, token);
        redisTemplate.expire(whitelistTokenCacheKey, whitelistTokenCacheDuration);
    }

    private boolean existsWhitelistToken(String token, SecretKey secretKey) {
        var memberId = TokenUtils.getMemberId(token, secretKey);
        var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
        return redisTemplate.opsForSet().isMember(whitelistTokenCacheKey, token);
    }

    private void removeWhitelistToken(String token, SecretKey secretKey) {
        var memberId = TokenUtils.getMemberId(token, secretKey);
        var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
        redisTemplate.opsForSet().remove(whitelistTokenCacheKey, token);
    }

}
