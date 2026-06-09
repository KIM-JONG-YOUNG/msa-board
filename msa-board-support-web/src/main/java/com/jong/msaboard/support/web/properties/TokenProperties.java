package com.jong.msaboard.support.web.properties;

import java.time.Duration;
import javax.crypto.SecretKey;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record TokenProperties(
    Details accessToken,
    Details refreshToken
) {

    public TokenProperties {
        accessToken = accessToken != null ? accessToken : Details.builder().build();
        refreshToken = refreshToken != null ? refreshToken : Details.builder().build();
    }

    @Builder
    public record Details(
        SecretKey secretKey,
        Duration validDuration
    ) {}

}
