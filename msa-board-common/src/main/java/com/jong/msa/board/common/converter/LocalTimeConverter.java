package com.jong.msa.board.common.converter;

import com.jong.msa.board.common.constants.DateTimeFormats;
import java.time.LocalTime;
import org.springframework.core.convert.converter.Converter;

public final class LocalTimeConverter {

    public enum StringToLocalTime implements Converter<String, LocalTime> {

        INSTANCE;

        @Override
        public LocalTime convert(String source) {
            return LocalTime.parse(source, DateTimeFormats.TIME_FORMATTER);
        }
    }

    public enum LocalTimeToString implements Converter<LocalTime, String> {

        INSTANCE;

        @Override
        public String convert(LocalTime source) {
            return source.format(DateTimeFormats.TIME_FORMATTER);
        }
    }

}
