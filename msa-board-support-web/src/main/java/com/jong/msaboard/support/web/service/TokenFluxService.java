package com.jong.msaboard.support.web.service;

import com.jong.msaboard.common.constants.RedisKeyPrefixes;
import com.jong.msaboard.support.web.condition.ConditionalOnWebFluxSecurity;
import com.jong.msaboard.support.web.error.SecurityErrorCode;
import com.jong.msaboard.support.web.exception.RevokedJwtException;
import com.jong.msaboard.support.web.properties.TokenProperties;
import com.jong.msaboard.support.web.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnWebFluxSecurity
public class TokenFluxService {

    private final TokenProperties tokenProperties;

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<? extends Authentication> getAuthenticationFromAccessToken(String accessToken) {
        var secretKey = tokenProperties.accessToken().secretKey();
        return Mono.zip(
                Mono.fromSupplier(() -> TokenUtils.getMemberId(accessToken, secretKey)),
                Mono.fromSupplier(() -> TokenUtils.getMemberGroup(accessToken, secretKey))
            )
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(tuple -> {
                var memberId = tuple.getT1();
                var memberGroup = tuple.getT2();
                var whitelistTokenCacheKey = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX + memberId;
                return reactiveRedisTemplate.opsForSet().isMember(whitelistTokenCacheKey, accessToken)
                    .filter(existsWhitelist -> existsWhitelist)
                    .switchIfEmpty(Mono.error(new RevokedJwtException("취소된 Access Token 입니다.")))
                    .then(Mono.fromSupplier(() -> {
                        var authorities = Set.of(new SimpleGrantedAuthority("ROLE_" + memberGroup.name()));
                        return new UsernamePasswordAuthenticationToken(memberId, null, authorities);
                    }));
            })
            .onErrorMap(exception -> {
                if (exception instanceof RevokedJwtException) {
                    return SecurityErrorCode.REVOKED_ACCESS_TOKEN.toException();
                } else if (exception instanceof ExpiredJwtException) {
                    return SecurityErrorCode.EXPIRED_ACCESS_TOKEN.toException();
                }
                return SecurityErrorCode.INVALID_ACCESS_TOKEN.toException();
            });
    }

}
