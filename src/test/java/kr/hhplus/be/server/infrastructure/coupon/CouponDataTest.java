package kr.hhplus.be.server.infrastructure.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.infrastructure.jpa.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.jpa.coupon.issuedcoupon.IssuedCouponJpaRepository;
import kr.hhplus.be.server.infrastructure.jpa.user.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

@SpringBootTest
public class CouponDataTest {

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private IssuedCouponJpaRepository issuedCouponJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    User user;

    void setUp(int count) {
        issuedCouponJpaRepository.deleteAll();
        user = User.builder().build();

        for (int i = 0; i < count; i++) {
            Coupon coupon = Coupon.builder().build();

            userJpaRepository.save(user);
            couponJpaRepository.save(coupon);

            issuedCouponJpaRepository.save(
                IssuedCoupon.builder()
                    .user(
                        user
                    )
                    .coupon(
                        coupon
                    )
                    .status(
                        CouponStatus.NOT_USED
                    )
                    .expireDate(
                        LocalDate.now().plus(i, ChronoUnit.DAYS)
                    )
                    .build()
            );
        }
    }

    /**
     * 사용자 보유쿠폰 목록 조회 테스트
     * - 페이징 처리가 올바른지 테스트
     */
    @Test
    @DisplayName("case - 사용자 보유쿠폰 목록 조회 시 페이징 처리가 올바른지 테스트")
    public void test1(){
        // given
        int setUpCount = 25;
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) setUpCount / pageSize);

        // setUpCount 수 만큼 insert
        setUp(setUpCount);

        // 특정 user의 ID 입력
        long userId = user.getId();

        // when
        for (int page = 0; page < totalPages; page++) {
            int start = page * pageSize;
            int end = Math.min(start + pageSize, setUpCount);

            // 각 페이지 별 아이템 수 기대값
            int itemsPerPage = end - start;

            // page 별 pageable 생성
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<IssuedCoupon> issuedCoupons = issuedCouponRepository.selectIssuedCouponList(userId, pageable);

            // then
            // 조회 시 페이지별 수량 비교
            assertEquals(itemsPerPage, issuedCoupons.getNumberOfElements());
        }

    }

}
