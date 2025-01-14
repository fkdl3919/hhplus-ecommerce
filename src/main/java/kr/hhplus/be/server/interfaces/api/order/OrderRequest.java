package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.command.OrderCommand.Order;
import kr.hhplus.be.server.domain.order.command.OrderCommand.Order.OrderItemCommand;

public record OrderRequest(
    @Schema(description = "사용자 userId")
    long userId,

    @Schema(description = "보유쿠폰 userId")
    Long issuedCouponId,

    @Schema(description = "상품 목록")
    List<OrderCommand.Order.OrderItemCommand> products
){

    public static OrderCommand.Order toOrder(OrderRequest request) {
        return OrderCommand.Order.builder().userId(request.userId).issuedCouponId(request.issuedCouponId).products(request.products).build();
    }

}
