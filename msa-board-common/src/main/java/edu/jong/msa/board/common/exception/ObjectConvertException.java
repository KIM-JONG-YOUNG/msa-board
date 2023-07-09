package edu.jong.msa.board.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ObjectConvertException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Object origin;
	
	private final String convertType;
	
	@Override
	public String getMessage() {
		return String.format("'%s'을(를) '%s'타입으로 변환하는데 실패했습니다.", origin, convertType);
	}
	
}
