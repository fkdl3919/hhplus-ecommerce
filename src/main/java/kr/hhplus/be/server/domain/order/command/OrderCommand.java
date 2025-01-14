package kr.hhplus.be.server.domain.order.command;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

public class OrderCommand{

    @Builder
    public record Order(

        @Schema(description = "사용자 userId")
        long userId,

        @Schema(description = "보유쿠폰 userId")
        Long issuedCouponId,

        @Schema(description = "상품 목록")
        List<OrderItemCommand> products,

        @Schema(description = "상품 목록")
        Long orderPrice
        ) {

        public record OrderItemCommand(
            @Schema(description = "상품 userId")
            long productId,

            @Schema(description = "상품 수량")
            int quantity
        ) {
        }

    }

}
