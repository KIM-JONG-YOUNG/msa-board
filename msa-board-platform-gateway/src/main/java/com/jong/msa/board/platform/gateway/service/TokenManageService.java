package com.jong.msa.board.platform.gateway.service;

import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.platform.gateway.exception.RevokedJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenManageService.Properties.class)
public class TokenManageService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private final Properties properties;

    private Mono<String> generateToken(TokenDetail tokenDetail, PropertyDetails property) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime expiredDateTime = currentDateTime.plusSeconds(property.expireTime());
        return Mono.fromSupplier(() -> Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(tokenDetail.id().toString())
                .issuedAt(Timestamp.valueOf(currentDateTime))
                .expiration(Timestamp.valueOf(expiredDateTime))
                .signWith(property.secretKey())
                .compact())
            .flatMap(token -> {
                String cachingKey = "token::" + tokenDetail.id() + "::" + token;
                String cachingValue = tokenDetail.group().name();
                Duration cachingDuration = Duration.between(currentDateTime, expiredDateTime);
                return reactiveRedisTemplate.opsForValue().set(cachingKey, cachingValue, cachingDuration)
                    .flatMap(isSuccess -> isSuccess
                        ? Mono.just(token)
                        : Mono.error(new RuntimeException("Redis에 토큰 정보를 저장하는데 오류가 발생했습니다.")));
            });
    }

    public Mono<TokenPair> generateTokenPair(TokenDetail tokenDetail) {
        return Mono.zip(
                generateToken(tokenDetail, properties.accessToken()),
                generateToken(tokenDetail, properties.refreshToken()))
            .map(tokens -> TokenPair.builder()
                .accessToken(tokens.getT1())
                .refreshToken(tokens.getT2())
                .build());
    }

    private Mono<TokenDetail> extractTokenDetail(String token, PropertyDetails property) {
        return Mono.fromSupplier(() -> Jwts.parser()
                .verifyWith(property.secretKey()).build()
                .parseSignedClaims(token)
                .getPayload())
            .flatMap(claims -> {
                String subject = claims.getSubject();
                String cachingKey = "token::" + subject + "::" + token;
                return reactiveRedisTemplate.opsForValue()
                    .get(cachingKey)
                    .flatMap(cachingValue -> StringUtils.hasText(cachingValue)
                        ? Mono.just(cachingValue)
                        : Mono.error(new RevokedJwtException("사용할 수 없는 Token 입니다.")))
                    .map(Group::valueOf)
                    .map(group -> TokenDetail.builder()
                        .id(UUID.fromString(subject))
                        .group(group)
                        .build());
            });
    }

    public Mono<TokenDetail> extractAccessTokenDetail(String accessToken) {
        return extractTokenDetail(accessToken, properties.accessToken());
    }

    public Mono<TokenDetail> extractRefreshTokenDetail(String refreshToken) {
        return extractTokenDetail(refreshToken, properties.refreshToken());
    }

    public Mono<Void> revokeAccessToken(String accessToken) {
        return extractTokenDetail(accessToken, properties.accessToken())
            .map(tokenDetail -> "token::" + tokenDetail.id() + "::" + accessToken)
            .flatMap(cachingKey -> reactiveRedisTemplate.delete(cachingKey)).then()
            .onErrorResume(JwtException.class, e -> Mono.empty());
    }

    public Mono<Void> revokeRefreshToken(String refreshToken) {
        return extractTokenDetail(refreshToken, properties.refreshToken())
            .map(tokenDetail -> "token::" + tokenDetail.id() + "::" + refreshToken)
            .flatMap(cachingKey -> reactiveRedisTemplate.delete(cachingKey)).then()
            .onErrorResume(JwtException.class, e -> Mono.empty());
    }

    @Builder
    public record TokenPair(String accessToken, String refreshToken) {}

    @Builder
    public record TokenDetail(UUID id, Group group) {}

    @Builder
    @ConfigurationProperties(prefix = "token")
    public record Properties(
        PropertyDetails accessToken,
        PropertyDetails refreshToken
    ) {}

    @Builder
    public record PropertyDetails(
        SecretKey secretKey,
        long expireTime
    ) {}


}
