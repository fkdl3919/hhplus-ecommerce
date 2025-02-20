package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.enums.OutboxOrderStatus;
import lombok.Builder;

public class OrderEvent {

    @Builder
    public record OrderRequest(
        Long userId,
        Long orderId,
        OrderCommand.Order command
    ) {
    }

}
