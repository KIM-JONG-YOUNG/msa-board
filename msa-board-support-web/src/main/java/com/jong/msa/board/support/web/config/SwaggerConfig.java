package com.jong.msa.board.support.web.config;

import com.fasterxml.jackson.databind.JavaType;
import com.jong.msa.board.common.constants.DateTimeFormats;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.springdoc.core.configuration.SpringDocConfiguration")
public class SwaggerConfig {

    @Bean
    PropertyCustomizer timePropertyCustomizer() {
        return (property, type) -> {

            if (!(type.getType() instanceof JavaType javaType)) {
                return property;
            }

            Class<?> clazz = javaType.getRawClass();
            if (clazz == LocalTime.class) {
                return property.type("string").format("time")
                    .pattern(DateTimeFormats.TIME_FORMAT)
                    .example(LocalDateTime.now().format(DateTimeFormats.TIME_FORMATTER));
            } else if (clazz == LocalDate.class) {
                return property.type("string").format("date")
                    .pattern(DateTimeFormats.DATE_FORMAT)
                    .example(LocalDateTime.now().format(DateTimeFormats.DATE_FORMATTER));
            } else if (clazz == LocalDateTime.class) {
                return property.type("string").format("date-time")
                    .pattern(DateTimeFormats.DATE_TIME_FORMAT)
                    .example(LocalDateTime.now().format(DateTimeFormats.DATE_TIME_FORMATTER));
            }
            return property;
        };
    }

    @Bean
    PropertyCustomizer enumPropertyCustomizer() {
        return (property, type) -> {

            if (!(type.getType() instanceof JavaType javaType)) {
                return property;
            }

            Class<?> clazz = javaType.getRawClass();
            if (clazz.isEnum()) {
                property.setEnum(Arrays.stream(clazz.getEnumConstants())
                    .map(Enum.class::cast)
                    .map(Enum::name)
                    .toList());
            }
            return property;
        };
    }

}
