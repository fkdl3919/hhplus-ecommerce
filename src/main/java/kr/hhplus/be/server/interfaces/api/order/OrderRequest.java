package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.dto.OrderCommand;

public record OrderRequest(

    @Schema(description = "사용자 id")
    long userId,

    @Schema(description = "쿠폰 id")
    long couponId,

    @Schema(description = "상품 id")
    long productId,

    @Schema(description = "상품 수량")
    long stock
) {

    public OrderCommand toCommand() {
        return new OrderCommand(userId, couponId, productId, stock);
    }

}
