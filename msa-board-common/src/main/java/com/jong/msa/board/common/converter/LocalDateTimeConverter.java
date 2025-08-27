package com.jong.msa.board.common.converter;

import com.jong.msa.board.common.constants.DateTimeFormats;
import java.time.LocalDateTime;
import org.springframework.core.convert.converter.Converter;

public final class LocalDateTimeConverter {

    public enum StringToLocalDateTime implements Converter<String, LocalDateTime> {

        INSTANCE;

        @Override
        public LocalDateTime convert(String source) {
            return LocalDateTime.parse(source, DateTimeFormats.DATE_TIME_FORMATTER);
        }
    }

    public enum LocalDateTimeToString implements Converter<LocalDateTime, String> {

        INSTANCE;

        @Override
        public String convert(LocalDateTime source) {
            return source.format(DateTimeFormats.DATE_TIME_FORMATTER);
        }
    }

}
