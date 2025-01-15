package kr.hhplus.be.server.domain.order;

import java.util.List;

public interface OrderRepository {

    Order save(Order order);

    OrderItem saveOrderItem(OrderItem orderItem);

    List<OrderItem> findOrderItemListByOrderId(Long orderId);

}
