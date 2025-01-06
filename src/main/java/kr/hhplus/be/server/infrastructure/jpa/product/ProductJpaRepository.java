package kr.hhplus.be.server.infrastructure.jpa.product;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<User, Long> {

}
