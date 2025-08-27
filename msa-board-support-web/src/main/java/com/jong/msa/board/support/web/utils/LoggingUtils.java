package com.jong.msa.board.support.web.utils;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

@Slf4j
public class LoggingUtils {

    public static final int MAX_BODY_LENGTH = 1000;

    public static final String EMPTY_BODY = "<EMPTY-BODY>";
    public static final String NON_LOGGABLE_BODY = "<NON-LOGGABLE-BODY>";

    public static boolean isLoggableContentType(MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        return MediaType.APPLICATION_JSON.isCompatibleWith(contentType)
            || MediaType.APPLICATION_XML.isCompatibleWith(contentType)
            || MediaType.APPLICATION_YAML.isCompatibleWith(contentType)
            || MediaType.TEXT_MARKDOWN.isCompatibleWith(contentType)
            || MediaType.TEXT_HTML.isCompatibleWith(contentType)
            || MediaType.TEXT_PLAIN.isCompatibleWith(contentType);
    }

    public static String getLoggingBody(MediaType contentType, byte[] bytes) {

        if (!isLoggableContentType(contentType)) {
            return NON_LOGGABLE_BODY;
        }

        if (bytes == null && bytes.length == 0) {
            return EMPTY_BODY;
        }

        String body = new String(bytes, StandardCharsets.UTF_8);
        int bodyLength = StringUtils.hasText(body) ? body.length() : 0;
        return bodyLength > MAX_BODY_LENGTH
            ? body.substring(0, MAX_BODY_LENGTH) + "...[length=" + bodyLength + "]"
            : body;
    }

    public static String getLoggingBody(String contentType, byte[] bytes) {
        try {
            return getLoggingBody(MediaType.parseMediaType(contentType), bytes);
        } catch (Exception e) {
            log.warn("Logging 할 수 없는 Content-Type 입니다. ({})", contentType);
            return NON_LOGGABLE_BODY;
        }
    }


}
