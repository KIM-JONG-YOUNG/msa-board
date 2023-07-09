package edu.jong.msa.board.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import edu.jong.msa.board.common.exception.ObjectConvertException;
import edu.jong.msa.board.common.type.DBCodeEnum.State;
import edu.jong.msa.board.common.utils.ObjectMapperUtils;
import edu.jong.msa.board.web.exception.ParamValidException;

@Component
public class StateParamConverter implements Converter<String, State> {

	@Override
	public State convert(String source) {
		try {
			return ObjectMapperUtils.toEnum(source, State.class);
		} catch (ObjectConvertException e) {
			throw new ParamValidException(e.getMessage());
		}
	}

}
