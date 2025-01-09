package kr.hhplus.be.server.infrastructure.jpa.product;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select a from Product a where a.id = :id")
    Product findProductWithLock(Long id);
}
