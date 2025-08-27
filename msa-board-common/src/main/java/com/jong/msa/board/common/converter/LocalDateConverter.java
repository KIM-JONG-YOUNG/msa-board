package com.jong.msa.board.common.converter;

import com.jong.msa.board.common.constants.DateTimeFormats;
import java.time.LocalDate;
import org.springframework.core.convert.converter.Converter;

public final class LocalDateConverter {

    public enum StringToLocalDate implements Converter<String, LocalDate> {

        INSTANCE;

        @Override
        public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormats.DATE_FORMATTER);
        }
    }

    public enum LocalDateToString implements Converter<LocalDate, String> {

        INSTANCE;

        @Override
        public String convert(LocalDate source) {
            return source.format(DateTimeFormats.DATE_FORMATTER);
        }
    }

}
