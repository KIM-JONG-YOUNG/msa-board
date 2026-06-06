package com.jong.msaboard.support.infra.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.jong.msaboard.common.constants.DateTimeFormatters;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.indentOutput(true)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .featuresToDisable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .modules(new JavaTimeModule()
                .addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatters.TIME))
                .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatters.DATE))
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatters.DATE_TIME))
                .addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatters.TIME))
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatters.DATE))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatters.DATE_TIME)));
    }

}
