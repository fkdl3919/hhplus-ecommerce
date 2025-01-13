package kr.hhplus.be.server.domain.product;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import kr.hhplus.be.server.domain.product.info.ProductInfo;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product findProductWithLock(long id) {
        return productRepository.findProductWithLock(id).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
    }

    public PageImpl<ProductInfo> selectProductList(Pageable pageable) {
        return ProductInfo.toPaging(productRepository.selectProductList(pageable));
    }

    public List<ProductInfo> selectTopSellingProductList() {
        List<Product> products = productRepository.selectTopSellingProductList();
        return ProductInfo.toInfos(products);
    }
}
