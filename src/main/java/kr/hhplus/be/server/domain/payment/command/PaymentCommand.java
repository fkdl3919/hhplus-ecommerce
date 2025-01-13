package kr.hhplus.be.server.domain.payment.command;

public record PaymentCommand(
    Long userId,
    Long orderId,
    Long originalPrice,
    Long payPrice
) {

}
