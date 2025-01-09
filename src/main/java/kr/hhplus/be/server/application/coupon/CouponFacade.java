package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponInfo;
import kr.hhplus.be.server.application.coupon.dto.IssuedCouponInfo;
import kr.hhplus.be.server.application.product.dto.ProductInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;

    public void issueCoupon(long id, long userId) {
        couponService.issueCoupon(id, userId);
    }

    public PageImpl<IssuedCouponInfo> selectIssuedCouponList(long userId, Pageable pageable) {
        return IssuedCouponInfo.toPaging(couponService.selectIssuedCouponList(userId, pageable));
    }
}
