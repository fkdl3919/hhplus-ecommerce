package kr.hhplus.be.server.domain.product.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class ProductCommand {

    @Builder
    public record Get(
        @Schema(description = "상품 userId")
        long productId
    ){
    }

    @Builder
    public record Deduct(
        @Schema(description = "상품 userId")
        long productId,

        @Schema(description = "상품 수량")
        int quantity
    ){
    }

}
