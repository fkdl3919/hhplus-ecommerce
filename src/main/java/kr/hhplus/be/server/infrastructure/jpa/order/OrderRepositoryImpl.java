package kr.hhplus.be.server.infrastructure.jpa.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.command.OrderCommand.OrderItemCommand;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }
}
