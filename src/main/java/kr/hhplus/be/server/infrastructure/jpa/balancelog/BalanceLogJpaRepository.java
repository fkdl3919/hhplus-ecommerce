package kr.hhplus.be.server.infrastructure.jpa.balancelog;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceLogJpaRepository extends JpaRepository<User, Long> {

}
