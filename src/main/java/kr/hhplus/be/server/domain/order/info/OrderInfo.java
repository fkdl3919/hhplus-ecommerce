package kr.hhplus.be.server.domain.order.info;

import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.user.User;

public record OrderInfo(
    Long id,
    Long userId,
    Long issuedCouponId,
    OrderStatus status,
    Long orderPrice
) {

    public static OrderInfo of(Order order) {
        return new OrderInfo(order.getId(), order.getUserId(), order.getIssuedCouponId(), order.getStatus(), order.getOrderPrice());
    }

}
