package kr.hhplus.be.server.infrastructure.jpa.payment;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<User, Long> {

}
