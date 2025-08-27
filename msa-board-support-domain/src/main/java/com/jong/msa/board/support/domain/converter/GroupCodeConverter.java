package com.jong.msa.board.support.domain.converter;

import com.jong.msa.board.common.enums.Group;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GroupCodeConverter implements AttributeConverter<Group, Short> {

    @Override
    public Short convertToDatabaseColumn(Group attribute) {
        return switch (attribute) {
            case ADMIN -> 1;
            case USER -> 2;
            case null -> null;
        };
    }

    @Override
    public Group convertToEntityAttribute(Short dbData) {
        return switch (dbData) {
            case 1 -> Group.ADMIN;
            case 2 -> Group.USER;
            case null -> null;
            default -> throw new EnumConstantNotPresentException(Group.class, dbData.toString());
        };
    }

}
