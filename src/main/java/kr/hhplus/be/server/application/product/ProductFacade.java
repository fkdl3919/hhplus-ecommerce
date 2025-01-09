package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    public PageImpl<ProductInfo> selectProductList(Pageable pageable) {
        return ProductInfo.toPaging(productService.selectProductList(pageable));
    }
}
