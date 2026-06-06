package com.jong.msaboard.support.infra.converter;

import com.jong.msaboard.common.type.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusAttributeConverter implements AttributeConverter<Status, Byte> {

    @Override
    public Byte convertToDatabaseColumn(Status attribute) {
        if (attribute == null) {
            return null;
        }
        return switch (attribute) {
            case ACTIVE -> 1;
            case INACTIVE -> 0;
            case null -> null;
        };
    }

    @Override
    public Status convertToEntityAttribute(Byte dbData) {
        if (dbData == null) {
            return null;
        }
        return switch (dbData) {
            case 1 -> Status.ACTIVE;
            case 0 -> Status.INACTIVE;
            default -> {
                var message = "정의되지 않은 enum code 입니다. (class=%s, code=%s)";
                throw new RuntimeException(message.formatted(Status.class, dbData));
            }

        };
    }

}
