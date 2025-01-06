package kr.hhplus.be.server.infrastructure.jpa.orderitem;

import kr.hhplus.be.server.domain.orderitem.OrderItem;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {

}
