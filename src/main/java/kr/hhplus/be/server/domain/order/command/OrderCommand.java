package kr.hhplus.be.server.domain.order.command;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record OrderCommand(
    @Schema(description = "사용자 id")
    long userId,

    @Schema(description = "보유쿠폰 id")
    Long issuedCouponId,

    @Schema(description = "상품 목록")
    List<OrderItemCommand> products
) {

    public record OrderItemCommand(
        @Schema(description = "상품 id")
        long productId,

        @Schema(description = "상품 수량")
        long quantity
    ) {
    }
}
