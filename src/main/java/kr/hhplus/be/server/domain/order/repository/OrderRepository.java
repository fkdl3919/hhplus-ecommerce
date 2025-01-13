package kr.hhplus.be.server.domain.order.repository;

import java.util.List;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.command.OrderCommand.OrderItemCommand;

public interface OrderRepository {

    Order save(Order order);

    OrderItem saveOrderItem(OrderItem orderItem);

    List<OrderItem> findOrderItemListByOrderId(Long orderId);

}
