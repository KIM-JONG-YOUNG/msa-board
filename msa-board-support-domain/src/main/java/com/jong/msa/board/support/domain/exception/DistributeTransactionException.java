package com.jong.msa.board.support.domain.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DistributeTransactionException extends RuntimeException {

    private final String lockName;

    private DistributeTransactionException(String lockName, String message) {
        super(message);
        this.lockName = lockName;
    }

    private DistributeTransactionException(String lockName, Throwable cause) {
        super(cause);
        this.lockName = lockName;
    }

    public static DistributeTransactionException waitTimeout(String lockName) {
        String message = String.format("Redisson Lock 대기시간을 초과하였습니다. (lockName: %s)", lockName);
        return new DistributeTransactionException(lockName, message);
    }

    public static DistributeTransactionException leaseTimeout(String lockName) {
        String message = String.format("Redisson Lock 실행시간을 초과하였습니다. (lockName: %s)", lockName);
        return new DistributeTransactionException(lockName, message);
    }

    public static DistributeTransactionException causeBy(String lockName, Throwable cause) {
        return new DistributeTransactionException(lockName, cause);
    }

}
