package kr.hhplus.be.server.interfaces.api.product;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.hhplus.be.server.application.product.dto.ProductInfo;
import kr.hhplus.be.server.application.product.dto.ProductPage;
import kr.hhplus.be.server.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ProductResponse {

    // paing 용
    public record Paging(

        @Schema(description = "상품 목록")
        List<ProductInfo> item,

        @Schema(description = "페이징 정보")
        Pageable pageInfo,

        @Schema(description = "총 상품 수")
        long totalCount,

        @Schema(description = "총 페이지 수")
        int totalPage
    ) {
        public static Paging from(ProductPage pageInfo) {
            return new Paging(pageInfo.productInfos(), pageInfo.pageInfo(), pageInfo.totalCount(), pageInfo.totalPage());
        }
    }
}
