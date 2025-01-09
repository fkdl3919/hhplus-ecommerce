package kr.hhplus.be.server.application.product.dto;

import java.util.List;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.product.Product;

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

}
