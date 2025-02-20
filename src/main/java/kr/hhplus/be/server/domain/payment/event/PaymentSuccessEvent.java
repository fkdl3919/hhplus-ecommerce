package kr.hhplus.be.server.domain.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentSuccessEvent {

    private Long orderId;
    private Long paymentId;
    private Long userId;
    private Long price;

    public PaymentSuccessEvent(Long orderId, Long paymentId, Long userId, Long price) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.userId = userId;
        this.price = price;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPrice() {
        return price;
    }

}
