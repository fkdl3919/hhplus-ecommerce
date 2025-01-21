package kr.hhplus.be.server.application.coupon;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

@SpringBootTest
public class CouponIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    public User setUpUser() {
        User save = userRepository.save(User.builder().build());
        return save;
    }

    public Coupon setUpCoupon(int stock) {
        return couponRepository.save(Coupon.builder().stock(stock).build());
    }

    @BeforeEach
    public void setUp() throws Exception {
        for (int i = 0; i < 20; i++) {
            setUpUser();
        }
    }

    /**
     * 쿠폰발급 동시성 테스트
     */
    @Test
    @DisplayName("동시성 테스트 - 여러명의 유저가 쿠폰발급을 신청한 경우 발급 후 재고가 정확한지 테스트")
    public void test() throws InterruptedException {
        // given
        List<User> users = userRepository.findAll();

        // 쿠폰수량설정
        int initialStock = 100;
        Coupon coupon = setUpCoupon(initialStock);

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
        assertEquals(initialStock - users.size(), resultCoupon.getStock());

    }

}
