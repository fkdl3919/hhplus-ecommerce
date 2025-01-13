package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.command.OrderCommand.OrderItemCommand;

public record OrderRequest(

    @Schema(description = "사용자 id")
    long userId,

    @Schema(description = "쿠폰 id")
    long couponId,

    @Schema(description = "상품 목록")
    List<OrderItemRequest> products
) {

    /**
     * 상품 리스트 정보 request
     * @param productId
     * @param quantity
     */
    public record OrderItemRequest(

        @Schema(description = "상품 id")
        long productId,

        @Schema(description = "상품 수량")
        int quantity

    ) {
        public OrderItemCommand toCommand() {
            return new OrderItemCommand(productId, quantity);
        }
    }

    public OrderCommand toCommand() {
        return new OrderCommand(userId, couponId, products.stream().map(OrderItemRequest::toCommand).collect(Collectors.toList()));
    }

}
