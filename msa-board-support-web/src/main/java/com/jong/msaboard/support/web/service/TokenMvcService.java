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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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

        var secretKey = tokenProperties.accessToken().secretKey();
        var validDuration = tokenProperties.accessToken().validDuration();
        var accessToken = TokenUtils.generateToken(memberId, memberGroup, secretKey, validDuration);

        var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
        var whitelistTokenCacheDuration = Duration.ofMillis(Math.max(
            redisTemplate.getExpire(whitelistTokenCacheKey, TimeUnit.MILLISECONDS),
            validDuration.toMillis()
        ));
        redisTemplate.opsForSet().add(whitelistTokenCacheKey, accessToken);
        redisTemplate.expire(whitelistTokenCacheKey, whitelistTokenCacheDuration);

        return accessToken;
    }

    public String generateRefreshToken(UUID memberId) {

        var secretKey = tokenProperties.refreshToken().secretKey();
        var validDuration = tokenProperties.refreshToken().validDuration();
        var refreshToken = TokenUtils.generateToken(memberId, null, secretKey, validDuration);

        var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
        var whitelistTokenCacheDuration = Duration.ofMillis(Math.max(
            redisTemplate.getExpire(whitelistTokenCacheKey, TimeUnit.MILLISECONDS),
            validDuration.toMillis()
        ));
        redisTemplate.opsForSet().add(whitelistTokenCacheKey, refreshToken);
        redisTemplate.expire(whitelistTokenCacheKey, whitelistTokenCacheDuration);

        return refreshToken;
    }

    public Authentication getAuthenticationFromAccessToken(String accessToken) {
        try {

            var secretKey = tokenProperties.accessToken().secretKey();
            var memberId = TokenUtils.getMemberId(accessToken, secretKey);
            var memberGroup = TokenUtils.getMemberGroup(accessToken, secretKey);

            var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
            if (!redisTemplate.opsForSet().isMember(whitelistTokenCacheKey, accessToken)) {
                throw new RevokedJwtException("취소된 Access Token 입니다.");
            }

            var authorities = Set.of(new SimpleGrantedAuthority("ROLE_" + memberGroup.name()));
            return new UsernamePasswordAuthenticationToken(memberId, null, authorities);

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

            var secretKey = tokenProperties.refreshToken().secretKey();
            var memberId = TokenUtils.getMemberId(refreshToken, secretKey);

            var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
            if (!redisTemplate.opsForSet().isMember(whitelistTokenCacheKey, refreshToken)) {
                throw new RevokedJwtException("취소된 Access Token 입니다.");
            }

            return memberId;

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

            var secretKey = tokenProperties.accessToken().secretKey();
            var memberId = TokenUtils.getMemberId(accessToken, secretKey);

            var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
            redisTemplate.opsForSet().remove(whitelistTokenCacheKey, accessToken);

        } catch (ExpiredJwtException e) {
            log.warn("이미 만료된 Access Token 입니다.");
        } catch (Exception e) {
            log.warn("유효하지 않은 Access Token 입니다.");
        }
    }

    public void revokeRefreshToken(String refreshToken) {
        try {

            var secretKey = tokenProperties.refreshToken().secretKey();
            var memberId = TokenUtils.getMemberId(refreshToken, secretKey);

            var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
            redisTemplate.opsForSet().remove(whitelistTokenCacheKey, refreshToken);

        } catch (ExpiredJwtException e) {
            log.warn("이미 만료된 Refresh Token 입니다.");
        } catch (Exception e) {
            log.warn("유효하지 않은 Refresh Token 입니다.");
        }
    }

    public void revokeMemberTokenAll(UUID memberId) {
        var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
        redisTemplate.delete(whitelistTokenCacheKey);
    }

}
