package kr.hhplus.be.server.application.coupon.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.annotation.DateTimeFormat;

public record IssuedCouponInfo(
    Long id,
    Long userId,
    Long couponId,
    Integer discountRate,
    CouponStatus status,

    @DateTimeFormat(pattern = "YYYY-MM-DD")
    LocalDate expireDate
) {
    public static IssuedCouponInfo of(IssuedCoupon issuedCoupon) {
        return new IssuedCouponInfo(
            issuedCoupon.getId(),
            issuedCoupon.getUser().getId(),
            issuedCoupon.getCoupon().getId(),
            issuedCoupon.getCoupon().getDiscountRate(),
            issuedCoupon.getStatus(),
            issuedCoupon.getExpireDate()
        );
    }

    public static List<IssuedCouponInfo> toInfos(List<IssuedCoupon> issuedCoupons){
        return issuedCoupons.stream().map(IssuedCouponInfo::of).collect(Collectors.toList());
    }

    public static PageImpl<IssuedCouponInfo> toPaging(Page<IssuedCoupon> issuedCoupons) {
        return new PageImpl(toInfos(issuedCoupons.getContent()), issuedCoupons.getPageable(), issuedCoupons.getTotalElements()) {};
    }

}
