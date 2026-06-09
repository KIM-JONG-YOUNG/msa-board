package com.jong.msaboard.support.web.utils;

import com.jong.msaboard.common.type.Group;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

public final class TokenUtils {

    private static final String MEMBER_ID_CLAIM_KEY = "memberId";
    private static final String MEMBER_GROUP_CLAIM_KEY = "memberGroup";

    public static String generateToken(UUID memberId, Group memberGroup, SecretKey secretKey, Duration validDuration) {
        var validTime = validDuration.toMillis();
        var nowDate = new Date();
        var expiredDate = new Date(nowDate.getTime() + validTime);
        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .claim(MEMBER_ID_CLAIM_KEY, memberId.toString())
            .claim(MEMBER_GROUP_CLAIM_KEY, memberGroup != null ? memberGroup.name() : null)
            .issuedAt(nowDate)
            .expiration(expiredDate)
            .signWith(secretKey)
            .compact();
    }

    public static Claims getClaims(String token, SecretKey secretKey) {
        return Jwts.parser()
            .verifyWith(secretKey).build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public static UUID getMemberId(String token, SecretKey secretKey) {
        var claims = getClaims(token, secretKey);
        try {
            return UUID.fromString((String) claims.get(MEMBER_ID_CLAIM_KEY));
        } catch (Exception e) {
            throw new JwtException("Token 상의 회원 ID 정보가 유효하지 않습니다.", e);
        }
    }

    public static Group getMemberGroup(String token, SecretKey secretKey) {
        var claims = getClaims(token, secretKey);
        try {
            return Group.valueOf((String) claims.get(MEMBER_GROUP_CLAIM_KEY));
        } catch (Exception e) {
            throw new JwtException("Token 상의 회원 그룹 정보가 유효하지 않습니다.", e);
        }
    }

}
