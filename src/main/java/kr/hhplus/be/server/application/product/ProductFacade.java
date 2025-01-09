package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductPage;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    public ProductPage selectProductList(Pageable pageable) {
        return ProductPage.from(productService.selectProductList(pageable));
    }
}
