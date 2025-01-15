package kr.hhplus.be.server.domain.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Page<Product> selectProductPaging(Pageable pageable);

    Optional<Product> findById (Long id);

    Optional<Product> findProductWithLock(Long id);

    Product save(Product build);

    List<Product> selectTopSellingProducts();
}
