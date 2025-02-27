package kr.hhplus.be.server.interfaces.scheduler.coupon;

import java.util.List;
import java.util.concurrent.TimeUnit;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.infrastructure.redis.DistributeLock;
import kr.hhplus.be.server.infrastructure.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final RedisRepository redisRepository;
    private final CouponService couponService;
    private final RedissonClient redissonClient;

    private static int FETCH_LIMIT = 200;
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    /**
     * 쿠폰 발급 스케줄러
     * 1분마다 실행
     * 분산락을 사용하여 스케줄러 중복 실행 방지
     * @throws InterruptedException
     */
//    @Scheduled(fixedRate = 1000)
    public void excute() throws InterruptedException {
        String LOCK_KEY = REDISSON_LOCK_PREFIX + "couponScheduler";
        RLock rLock = redissonClient.getLock(LOCK_KEY);

        try {
            boolean available = rLock.tryLock(10, TimeUnit.SECONDS);
            if (!available) {
                return;
            }
            if (rLock.isLocked()) log.info("Redisson Locked {}", LOCK_KEY);

            issuedCoupon();
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();
                log.info("Redisson UnLocked {}", LOCK_KEY);
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock {}", LOCK_KEY);
            }
        }
    }

    /**
     * 쿠폰 발급
     */
    public void issuedCoupon() {
        // Keys iteration
        Iterable<String> keys = redisRepository.getAllKeys("coupon:*");

        for (String key : keys) {

            int keySize = redisRepository.getSortedSetSize(key);
            if (keySize == 0){
                continue;
            }

            // coupon:1 -> 1
            Long couponId = Long.parseLong(key.split(":")[1]);

            List<String> userId = redisRepository.getSortedSetValues(key, 0, keySize > FETCH_LIMIT ? FETCH_LIMIT - 1 : keySize);
            userId.stream().forEach(u -> {
                IssuedCoupon issuedCoupon = null;
                try {
                    issuedCoupon = couponService.issueCoupon(couponId, Long.parseLong(u));
                } catch (Exception e) {
                    log.error("쿠폰 발급 실패: couponId-{}, userId-{}, error-{}", couponId, u, e.getMessage());
                } finally {
                    // 쿠폰 발급 후 삭제
                    redisRepository.deleteValue(key, u);

                    // 발급 이력 set으로 저장
                    if(issuedCoupon != null){
                        redisRepository.setSet("issuedCoupon:" + couponId, u);
                    }
                }
            });

        }
    }

}
