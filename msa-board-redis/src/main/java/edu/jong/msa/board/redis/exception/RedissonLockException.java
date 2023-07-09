package edu.jong.msa.board.redis.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class RedissonLockException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String lockKey;
	
	@Override
	public String getMessage() {
		return String.format("Not get redis lock. (key: '%s')", lockKey);
	}
	
}
