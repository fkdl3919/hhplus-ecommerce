package kr.hhplus.be.server.application.coupon;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructure.redis.RedisRepository;
import kr.hhplus.be.server.interfaces.scheduler.coupon.CouponScheduler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class CouponIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private CouponScheduler couponScheduler;

    @Autowired
    private RedisRepository redisRepository;

    private static int FETCH_LIMIT = 200;
    private final String COUPON_PREFIX = "coupon:";
    private final String ISSUED_COUPON_PREFIX = "issuedCoupon:";

    public User setUpUser() {
        User save = userRepository.save(User.builder().build());
        return save;
    }

    public Coupon setUpCoupon(int stock) {
        return couponRepository.save(Coupon.builder().stock(stock).build());
    }

    @BeforeEach
    public void setUp() throws Exception {
        for (int i = 0; i < 150; i++) {
            setUpUser();
        }
    }

    /**
     * Redis 요구사항으로 인해 Disabled 처리
     * 쿠폰발급 동시성 테스트
     */
    @Test
    @DisplayName("동시성 테스트 - 여러명의 유저가 쿠폰발급을 신청한 경우 발급 후 재고가 정확한지 테스트")
    @Disabled
    public void test() throws InterruptedException {
        // given
        List<User> users = userRepository.findAll();

        // 쿠폰수량설정
        int initialStock = 1000;
        Coupon coupon = setUpCoupon(initialStock);

        log.info(String.valueOf(coupon.getStock()));

        int threadCount = users.size();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        for (User user : users) {
            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(coupon.getId(), user.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        Coupon resultCoupon = couponService.getCoupon(coupon.getId());
        int expected = initialStock - users.size();
        assertEquals(expected < 0 ? 0 : expected, resultCoupon.getStock());

    }

    /**
     * 쿠폰 요청 테스트
     */
    @Test
    @DisplayName("case - 쿠폰 요청 후 Redis에 쿠폰요청이 저장되는지 테스트")
    public void test1() throws InterruptedException {
        // given
        List<User> users = userRepository.findAll();

        // 쿠폰수량설정
        int initialStock = 1000;
        Coupon coupon = setUpCoupon(initialStock);

        log.info(String.valueOf(coupon.getStock()));

        int threadCount = users.size();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        for (User user : users) {
            executorService.submit(() -> {
                try {
                    couponService.requestCoupon(coupon.getId(), user.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertEquals(users.size(), redisRepository.getSortedSetSize(COUPON_PREFIX + coupon.getId()));

    }

    /**
     * 쿠폰 발급 테스트
     */
    @Test
    @DisplayName("case - 쿠폰 발급 후 Redis에 쿠폰이 발급내역이 저장되었는지 테스트")
    public void test2() throws InterruptedException {
        // given
        List<User> users = userRepository.findAll();

        // 쿠폰수량설정
        int initialStock = 1000;
        Coupon coupon = setUpCoupon(initialStock);

        log.info(String.valueOf(coupon.getStock()));

        int threadCount = users.size();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // 쿠폰 요청
        for (User user : users) {
            executorService.submit(() -> {
                try {
                    couponService.requestCoupon(coupon.getId(), user.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // when
        couponScheduler.excute();

        // then
        assertEquals(users.size(), redisRepository.getSetSize(ISSUED_COUPON_PREFIX + coupon.getId()));

    }

    /**
     * 쿠폰 발급 테스트
     */
    @Test
    @DisplayName("case - 분산 서버 환경에서 스케줄러가 동시에 실행될 경우 쿠폰 발급 후 Redis에 쿠폰이 발급내역이 저장되었는지, 차감된 쿠폰 수는 주어진 user size 만큼 차감되었는지 테스트")
    public void test3() throws InterruptedException {
        // given
        List<User> users = userRepository.findAll();

        // 쿠폰수량설정
        int initialStock = 1000;
        Coupon coupon = setUpCoupon(initialStock);

        log.info(String.valueOf(coupon.getStock()));

        int threadCount = users.size();


        // 쿠폰 요청
        for (User user : users) {
            couponService.requestCoupon(coupon.getId(), user.getId());
        }

        // when
        int threadCount2 = 3;
        ExecutorService executorService2 = Executors.newFixedThreadPool(threadCount2);
        CountDownLatch countDownLatch2 = new CountDownLatch(threadCount2);

        for(int i = 0; i < threadCount2; i++){
            executorService2.submit(() -> {
                try {
                    couponScheduler.excute();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch2.countDown();
                }
            });
        }
        countDownLatch2.await();

        // then
        assertEquals(users.size(), redisRepository.getSetSize(ISSUED_COUPON_PREFIX + coupon.getId()));
        Coupon resultCoupon = couponService.getCoupon(coupon.getId());
        int expected = initialStock - users.size();
        assertEquals(expected < 0 ? 0 : expected, resultCoupon.getStock());

    }


}
