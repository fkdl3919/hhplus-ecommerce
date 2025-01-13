package kr.hhplus.be.server.domain.product.repository;

import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.info.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Page<Product> selectProductList(Pageable pageable);

    Optional<Product> findProductWithLock(Long id);

    Product save(Product build);

    List<Product> selectTopSellingProductList();
}
