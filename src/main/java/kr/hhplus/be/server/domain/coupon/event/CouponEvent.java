package kr.hhplus.be.server.domain.coupon.event;

import kr.hhplus.be.server.domain.order.command.OrderCommand;
import lombok.Builder;

public class CouponEvent {

    @Builder
    public record UsedCoupon(
        Long userId,
        Long orderId,
        Integer discountRate,
        OrderCommand.Order command
    ) {
    }

}
