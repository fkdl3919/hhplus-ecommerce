package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    OrderItem saveOrderItem(OrderItem orderItem);

    List<OrderItem> findOrderItemListByOrderId(Long orderId);

    Optional<Order> findById(Long orderId);
}
