package com.jong.msa.board.platform.gateway.converter;

import io.jsonwebtoken.security.Keys;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SecretKeyConverter implements Converter<String, SecretKey> {

    @Override
    public SecretKey convert(String source) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return Keys.hmacShaKeyFor(sha256.digest(source.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
