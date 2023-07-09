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
			throw new IllegalArgumentException("DB의 값을 Null로 저장할 수 없습니다.");
		} else {
			return (attribute == null) ? null : attribute.getCode();
		}
	}

	@Override
	public E convertToEntityAttribute(V dbData) {
		
		if (!this.nullable && dbData == null) {
			throw new IllegalArgumentException("DB의 값이 Null로 저장되어 있습니다.");
		} else {
			return (dbData == null) ? null : ObjectMapperUtils.toDBCodeEnum(dbData, this.enumType);
		}
	}

}
