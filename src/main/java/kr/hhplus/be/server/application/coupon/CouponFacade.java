package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;

    public void issueCoupon(long id, long userId) {
        couponService.issueCoupon(id, userId);
    }
}
