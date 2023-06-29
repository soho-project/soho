package work.soho.common.data.lock.exception;

/**
 * 加锁异常
 */
public class LockException extends RuntimeException{
    public LockException(String s) {
        super(s);
    }
}
