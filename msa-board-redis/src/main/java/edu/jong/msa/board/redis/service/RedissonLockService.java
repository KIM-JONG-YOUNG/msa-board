package edu.jong.msa.board.redis.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import edu.jong.msa.board.redis.exception.RedissonLockException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedissonLockService {

	private final RedissonClient redissonClient;

	@FunctionalInterface
	public interface LockCallable<T> {
		T call();
	}
	
	public <T> T excute(String lockKey, long waitTime, long leaseTime, LockCallable<T> callable) {

		RLock redissonLock = redissonClient.getLock(lockKey);

		try {
			if (redissonLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
				
				return callable.call();

			} else {
				throw new RedissonLockException(redissonLock.getName());
			}
		} catch (InterruptedException e) {
			throw new RedissonLockException(redissonLock.getName());
		} finally {
			if (redissonLock.isLocked()) redissonLock.unlock();
		}
	}
	
}
