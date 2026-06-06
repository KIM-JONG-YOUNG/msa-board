package com.jong.msaboard.support.infra.transaction;

import com.jong.msaboard.support.infra.condition.ConditionalOnJpa;
import com.jong.msaboard.support.infra.condition.ConditionalOnRedisson;
import com.jong.msaboard.support.infra.utils.MethodAspectUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LockTransactional {

    String name();

    String key();

    long waitSeconds() default 10;

    long leaseSeconds() default 5;

    @Aspect
    @Component
    @ConditionalOnJpa
    @ConditionalOnRedisson
    @RequiredArgsConstructor
    @Order(Ordered.LOWEST_PRECEDENCE)
    class Advisor {

        private final RedissonClient redissonClient;

        private final TransactionTemplate transactionTemplate;

        @Around("@annotation(com.jong.msaboard.support.infra.transaction.LockTransactional)")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

            var annotation = MethodAspectUtils.getAnnotation(joinPoint, LockTransactional.class);
            var lockName = StringUtils.hasText(annotation.key())
                ? annotation.name() + MethodAspectUtils.getParameter(joinPoint, annotation.key())
                : annotation.name();

            var lock = redissonClient.getLock(lockName);
            var waitSeconds = annotation.waitSeconds();
            var leaseSeconds = annotation.leaseSeconds();
            var transactionResult = executeInLock(joinPoint, lock, waitSeconds, leaseSeconds);

            if (transactionResult.throwable() != null) {
                throw transactionResult.throwable();
            }
            return transactionResult.result();
        }

        private Result executeInLock(ProceedingJoinPoint joinPoint, RLock lock, long waitSeconds, long leaseSeconds) {
            try {
                var isLockAcquired = lock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS);
                if (!isLockAcquired) {
                    return Result.waitTimeout(lock.getName(), waitSeconds);
                }
                return transactionTemplate.execute(status -> {
                    try {
                        var result = joinPoint.proceed();
                        var isLocked = lock.isLocked() && lock.isHeldByCurrentThread();
                        if (isLocked) {
                            return Result.success(result);
                        } else {
                            status.setRollbackOnly();
                            return Result.leaseTimeout(lock.getName(), leaseSeconds);
                        }
                    } catch (Throwable e) {
                        status.setRollbackOnly();
                        return Result.error(e);
                    }
                });
            } catch (Exception e) {
                return Result.error(e);
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    @With
    @Builder
    record Result(Object result, Throwable throwable) {

        public static Result success(Object result) {
            return Result.builder().result(result).build();
        }

        public static Result error(Throwable throwable) {
            return Result.builder().throwable(throwable).build();
        }

        public static Result waitTimeout(String lockName, long waitSeconds) {
            var message = "분산 트랜잭션 대기시간을 초과하였습니다. (lockName=%s, leaseSeconds=%s)";
            return error(new RuntimeException(message.formatted(lockName, waitSeconds)));
        }

        public static Result leaseTimeout(String lockName, long leaseSeconds) {
            var message = "분산 트랜잭션 처리시간을 초과하였습니다. (lockName=%s, leaseSeconds=%s)";
            return error(new RuntimeException(message.formatted(lockName, leaseSeconds)));
        }
    }

}
