package com.jong.msa.board.support.web.converter;

import com.jong.msa.board.common.constants.DateTimeFormats;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.core.convert.converter.Converter;

public final class JavaTimeParamConverter {

    public enum StringToLocalTime implements Converter<String, LocalTime> {

        INSTANCE;

        @Override
        public LocalTime convert(String source) {
            return LocalTime.parse(source, DateTimeFormats.TIME_FORMATTER);
        }
    }

    public enum StringToLocalDate implements Converter<String, LocalDate> {

        INSTANCE;

        @Override
        public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormats.DATE_FORMATTER);
        }
    }

    public enum StringToLocalDateTime implements Converter<String, LocalDateTime> {

        INSTANCE;

        @Override
        public LocalDateTime convert(String source) {
            return LocalDateTime.parse(source, DateTimeFormats.DATE_TIME_FORMATTER);
        }
    }

}
