package kr.hhplus.be.server.domain.order.info;

import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.user.User;

public record OrderInfo(
    Long id,
    Long userId,
    Long issuedCouponId,
    OrderStatus status
) {

    public static OrderInfo of(Order order) {
        return new OrderInfo(order.getId(), order.getUser().getId(), order.getIssuedCoupon() != null ? order.getIssuedCoupon().getId() : null, order.getStatus());
    }

}
