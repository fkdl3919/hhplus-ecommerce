package kr.hhplus.be.server.interfaces.consumer.payment;

import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.command.PaymentCommand.Pay;
import kr.hhplus.be.server.domain.payment.event.PaymentSuccessEvent;
import kr.hhplus.be.server.infrastructure.dataplatform.Dataplatform;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;

    // 데이터 플랫폼 전송
    @KafkaListener(topics = "${spring.kafka.topic.payment.success}", groupId = "outobx.payment")
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
        Dataplatform.sendData(event.getOrderId());
    }

    @KafkaListener(topics = "${spring.kafka.topic.coupon.used}", groupId = "payment.consumer")
    public void consumeCouponUsed(CouponEvent.UsedCoupon event) {
        Pay command = Pay.builder().userId(event.userId()).orderId(event.orderId()).orderPrice(event.command().orderPrice()).discountRate(event.discountRate()).build();
        paymentService.pay(command);
    }

}
