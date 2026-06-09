package com.jong.msaboard.support.web.converter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class SecretKeyConverter implements Converter<String, SecretKey> {

    @Override
    public SecretKey convert(String source) {
        try {
            var sha256 = MessageDigest.getInstance("SHA-256");
            byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
            byte[] hashedKey = sha256.digest(sourceBytes);
            return new SecretKeySpec(hashedKey, "HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("암호화 알고리즘 사용 중 오류가 발생했습니다.");
        }
    }

}
