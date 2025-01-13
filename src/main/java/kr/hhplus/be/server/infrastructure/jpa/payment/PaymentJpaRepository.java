package kr.hhplus.be.server.infrastructure.jpa.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p where p.order.id = :orderId")
    Payment findPayMentByOrderId(Long orderId);
}
