package edu.jong.msa.board.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import edu.jong.msa.board.common.exception.ObjectConvertException;
import edu.jong.msa.board.common.type.DBCodeEnum.Gender;
import edu.jong.msa.board.common.utils.ObjectMapperUtils;
import edu.jong.msa.board.web.exception.ParamValidException;

@Component
public class GenderParamConverter implements Converter<String, Gender> {

	@Override
	public Gender convert(String source) {
		try {
			return ObjectMapperUtils.toEnum(source, Gender.class);
		} catch (ObjectConvertException e) {
			throw new ParamValidException(e.getMessage());
		}
	}

}
