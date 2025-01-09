package kr.hhplus.be.server.application.product.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.hhplus.be.server.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//@Parameter(description = "상품 페이징 정보")
public record ProductPage(

    @Schema(description = "상품 목록")
    List<ProductInfo> productInfos,

    @Schema(description = "페이징 정보")
    Pageable pageInfo,

    @Schema(description = "총 상품 수")
    long totalCount,

    @Schema(description = "총 페이지 수")
    int totalPage
) {

    public static ProductPage from(Page<Product> pageInfo) {
        ProductPage productPage = new ProductPage(
            ProductInfo.toInfos(pageInfo.getContent()),
            pageInfo.getPageable(),
            pageInfo.getTotalElements(),
            pageInfo.getTotalPages()
        );
        return productPage;
    }

}
