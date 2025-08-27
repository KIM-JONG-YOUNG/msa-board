package com.jong.msa.board.support.domain.converter;

import com.jong.msa.board.common.enums.State;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StateCodeConverter implements AttributeConverter<State, Short> {

    @Override
    public Short convertToDatabaseColumn(State attribute) {
        return switch (attribute) {
            case ACTIVE -> 1;
            case INACTIVE -> 0;
            case null -> null;
        };
    }

    @Override
    public State convertToEntityAttribute(Short dbData) {
        return switch (dbData) {
            case 1 -> State.ACTIVE;
            case 0 -> State.INACTIVE;
            case null -> null;
            default -> throw new EnumConstantNotPresentException(State.class, dbData.toString());
        };
    }

}
