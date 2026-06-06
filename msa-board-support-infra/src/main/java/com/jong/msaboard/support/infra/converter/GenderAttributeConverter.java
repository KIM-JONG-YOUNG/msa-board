package com.jong.msaboard.support.infra.converter;

import com.jong.msaboard.common.type.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderAttributeConverter implements AttributeConverter<Gender, Character> {

    @Override
    public Character convertToDatabaseColumn(Gender attribute) {
        if (attribute == null) {
            return null;
        }
        return switch (attribute) {
            case MALE -> 'M';
            case FEMALE -> 'F';
        };
    }

    @Override
    public Gender convertToEntityAttribute(Character dbData) {
        if (dbData == null) {
            return null;
        }
        return switch (dbData) {
            case 'M' -> Gender.MALE;
            case 'F' -> Gender.FEMALE;
            default -> {
                var message = "정의되지 않은 enum code 입니다. (class=%s, code=%s)";
                throw new RuntimeException(message.formatted(Gender.class, dbData));
            }
        };
    }

}
