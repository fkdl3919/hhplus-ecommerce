package kr.hhplus.be.server.domain.product;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import kr.hhplus.be.server.application.product.dto.ProductInfo;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product findProductWithLock(Long id) {
        return productRepository.findProductWithLock(id).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
    }

    public Page<Product> selectProductList(Pageable pageable) {
        return productRepository.selectProductList(pageable);
    }
}
