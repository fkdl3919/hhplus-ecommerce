package kr.hhplus.be.server.interfaces.event.payment;

public class PaymentSuccessEvent {

    private final Long orderKey;
    private final Long paymentKey;

    public PaymentSuccessEvent(Long orderKey, Long paymentKey) {
        this.orderKey = orderKey;
        this.paymentKey = paymentKey;
    }

    public Long getOrderKey() {
        return orderKey;
    }

    public Long getPaymentKey() {
        return paymentKey;
    }

}
