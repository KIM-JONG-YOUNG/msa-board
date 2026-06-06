package com.jong.msaboard.support.infra.converter;

import com.jong.msaboard.common.type.Group;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GroupAttributeConverter implements AttributeConverter<Group, Byte> {

    @Override
    public Byte convertToDatabaseColumn(Group attribute) {
        if (attribute == null) {
            return null;
        }
        return switch (attribute) {
            case ADMIN -> 1;
            case USER -> 2;
        };
    }

    @Override
    public Group convertToEntityAttribute(Byte dbData) {
        if (dbData == null) {
            return null;
        }
        return switch (dbData) {
            case 1 -> Group.ADMIN;
            case 2 -> Group.USER;
            default -> {
                var message = "정의되지 않은 enum code 입니다. (class=%s, code=%s)";
                throw new RuntimeException(message.formatted(Group.class, dbData));
            }
        };
    }

}
