package com.jong.msa.board.support.domain.converter;

import com.jong.msa.board.common.enums.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderCodeConverter implements AttributeConverter<Gender, Character> {

    @Override
    public Character convertToDatabaseColumn(Gender attribute) {
        return switch (attribute) {
            case MALE -> 'M';
            case FEMALE -> 'F';
            case null -> null;
        };
    }

    @Override
    public Gender convertToEntityAttribute(Character dbData) {
        return switch (dbData) {
            case 'M' -> Gender.MALE;
            case 'F' -> Gender.FEMALE;
            case null -> null;
            default -> throw new EnumConstantNotPresentException(Gender.class, dbData.toString());
        };
    }

}
