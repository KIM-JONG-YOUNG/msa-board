package edu.jong.msa.board.domain.converter;

import javax.persistence.AttributeConverter;

import edu.jong.msa.board.common.type.DBCodeEnum;
import edu.jong.msa.board.common.utils.ObjectMapperUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public abstract class DBCodeAttributeConverter<E extends Enum<E> & DBCodeEnum<V>, V> implements AttributeConverter<E, V> {

	protected final Class<E> enumType;

	protected final boolean nullable;
	
	@Override
	public V convertToDatabaseColumn(E attribute) {

		if (!this.nullable && attribute == null) {
			throw new IllegalArgumentException("Can not save to DB as null.");
		} else {
			return (attribute == null) ? null : attribute.getCode();
		}
	}

	@Override
	public E convertToEntityAttribute(V dbData) {
		
		if (!this.nullable && dbData == null) {
			throw new IllegalArgumentException("Data in DB is stored as null.");
		} else {
			return (dbData == null) ? null : ObjectMapperUtils.toDBCodeEnum(dbData, this.enumType);
		}
	}

}
