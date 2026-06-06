package com.jong.msaboard.support.infra.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;
import org.springframework.util.StringUtils;

@Converter(autoApply = true)
public class UUIDAttributeConverter implements AttributeConverter<UUID, String> {

    @Override
    public String convertToDatabaseColumn(UUID attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public UUID convertToEntityAttribute(String dbData) {
        return StringUtils.hasText(dbData) ? UUID.fromString(dbData) : null;
    }

}
