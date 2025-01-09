package kr.hhplus.be.server.domain.product.repository;

import java.util.List;
import kr.hhplus.be.server.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Page<Product> selectProductList(Pageable pageable);
}
