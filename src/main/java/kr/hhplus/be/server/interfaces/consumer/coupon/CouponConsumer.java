package kr.hhplus.be.server.interfaces.consumer.coupon;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.coupon.outbox.OutboxCouponRepository;
import kr.hhplus.be.server.domain.order.enums.OutboxOrderStatus;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponConsumer {

    private final CouponService couponService;
    private final OutboxCouponRepository outboxCouponRepository;

    @KafkaListener(topics = "${spring.kafka.topic.order.request}", groupId = "coupon.consumer")
    public void consumeOrderRequest(OrderEvent.OrderRequest event) {
        couponService.useCoupon(event);
    }

    @KafkaListener(topics = "${spring.kafka.topic.coupon.used}", groupId = "outbox.coupon")
    public void consumeCouponUsed(CouponEvent.UsedCoupon event) {
        outboxCouponRepository.findById(event.command().issuedCouponId()).orElseThrow(EntityNotFoundException::new).setStatus(OutboxOrderStatus.CONFIRMED);
    }

}
