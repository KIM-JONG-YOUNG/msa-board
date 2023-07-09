package edu.jong.msa.board.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import edu.jong.msa.board.common.exception.ObjectConvertException;
import edu.jong.msa.board.common.type.DBCodeEnum.Group;
import edu.jong.msa.board.common.utils.ObjectMapperUtils;
import edu.jong.msa.board.web.exception.ParamValidException;

@Component
public class GroupParamConverter implements Converter<String, Group> {

	@Override
	public Group convert(String source) {
		try {
			return ObjectMapperUtils.toEnum(source, Group.class);
		} catch (ObjectConvertException e) {
			throw new ParamValidException(e.getMessage());
		}
	}

}
