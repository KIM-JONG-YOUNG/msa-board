package edu.jong.msa.board.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface DBCodeEnum<Code> {

	public Code getCode();

	@Getter
	@RequiredArgsConstructor
	public static enum State implements DBCodeEnum<Integer> {

		ACTIVE(1), DEACTIVE(0);

		private final Integer code;
	}

}
