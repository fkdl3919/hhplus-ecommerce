package kr.hhplus.be.server.infrastructure.redis;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributeLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Around("@annotation(kr.hhplus.be.server.infrastructure.redis.DistributeLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String couponId = joinPoint.getArgs()[0].toString();
        DistributeLock distributedLock = method.getAnnotation(DistributeLock.class);

        AtomicReference<Object> result = new AtomicReference<>();
        String key = REDISSON_LOCK_PREFIX + distributedLock.key() + ":" + couponId;
        RLock rLock = redissonClient.getLock(key);  // (1)

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.timeUnit());  // (2)
            if (!available) {
                return false;
            }
            if (rLock.isLocked()) log.info("Redisson Locked {}", key);

            TransactionTemplate aopForTransaction = new TransactionTemplate(transactionManager);

            aopForTransaction.execute((status) -> {
                try {
                    result.set(joinPoint.proceed());
                } catch (Throwable throwable) {
                    status.setRollbackOnly();
                    log.error("DistributeLockAop Error", throwable);
                }
                return null;
            });

            return result.get();

        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();   // (4)
                log.info("Redisson UnLocked {}", key);
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock {}", key);
            }
        }
    }
}