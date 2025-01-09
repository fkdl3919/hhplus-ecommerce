package kr.hhplus.be.server.application.product.dto;

import java.util.List;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public record ProductInfo(
    Long id,
    String name,
    Long price,
    Integer stock
) {

    public static ProductInfo of(Product product) {
        return new ProductInfo(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }

    public static List<ProductInfo> toInfos(List<Product> products){
        return products.stream().map(ProductInfo::of).collect(Collectors.toList());
    }

    public static PageImpl<ProductInfo> toPaging(Page<Product> products) {
        return new PageImpl(toInfos(products.getContent()), products.getPageable(), products.getTotalElements()) {};
    }
}
