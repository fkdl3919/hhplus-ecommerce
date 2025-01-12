package kr.hhplus.be.server.application.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderCommand(
    @Schema(description = "사용자 id")
    long userId,

    @Schema(description = "보유쿠폰 id")
    long issuedCouponId,

    @Schema(description = "상품 id")
    long productId,

    @Schema(description = "상품 수량")
    long stock
) {

}
