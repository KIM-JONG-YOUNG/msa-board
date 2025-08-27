package com.jong.msa.board.support.domain.annotation;

import com.jong.msa.board.common.utils.SpELUtils;
import com.jong.msa.board.support.domain.exception.DistributeTransactionException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeTransactional {

    String name();

    String key();

    long waitTime() default 10;

    long leaseTime() default 5;

    @Component
    @RequiredArgsConstructor
    @org.aspectj.lang.annotation.Aspect
    @ConditionalOnClass(name = {
        "org.redisson.api.RedissonClient",
        "org.springframework.data.jpa.repository.JpaRepository"
    })
    class Aspect {

        private final RedissonClient redissonClient;

        private final TransactionTemplate transactionTemplate;

        @Around("@annotation(com.jong.msa.board.support.domain.annotation.DistributeTransactional)")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            DistributeTransactional annotation = signature.getMethod().getAnnotation(DistributeTransactional.class);

            Map<String, Object> parameters = new HashMap<>();
            String[] parameterNames = signature.getParameterNames();
            Object[] parameterValues = joinPoint.getArgs();
            for (int i = 0; i < parameterNames.length; i++) {
                parameters.put(parameterNames[i], parameterValues[i]);
            }

            Object parseValue = SpELUtils.getValue(annotation.key(), parameters);
            String lockName = annotation.name() + parseValue;
            long waitTime = annotation.waitTime();
            long leaseTime = annotation.leaseTime();

            RLock lock = redissonClient.getLock(lockName);
            if (lock == null) {
                throw DistributeTransactionException.waitTimeout(lockName);
            }

            try {
                if (!lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                    throw DistributeTransactionException.waitTimeout(lockName);
                }

                return transactionTemplate.execute(status -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        status.setRollbackOnly();
                        throw DistributeTransactionException.causeBy(lockName, e);
                    } finally {
                        if (!status.isRollbackOnly() && lock.remainTimeToLive() < 500) {
                            status.setRollbackOnly();
                            throw DistributeTransactionException.leaseTimeout(lockName);
                        }
                    }
                });
            } catch (DistributeTransactionException e) {
                throw e.getCause() != null ? e.getCause() : e;
            } finally {
                lock.unlock();
            }
        }
    }

}
