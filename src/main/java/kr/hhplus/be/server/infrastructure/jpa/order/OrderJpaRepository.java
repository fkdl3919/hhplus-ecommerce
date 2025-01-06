package kr.hhplus.be.server.infrastructure.jpa.order;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<User, Long> {

}
