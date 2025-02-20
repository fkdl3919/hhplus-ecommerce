package kr.hhplus.be.server.interfaces.event.coupon;

import kr.hhplus.be.server.domain.coupon.outbox.OutboxCoupon;
import kr.hhplus.be.server.domain.coupon.outbox.OutboxCouponRepository;
import kr.hhplus.be.server.domain.order.enums.OutboxOrderStatus;
import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.order.outbox.OutboxOrder;
import kr.hhplus.be.server.infrastructure.kafka.coupon.CouponProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RequiredArgsConstructor
public class CouponListener {

    private final OutboxCouponRepository outboxCouponRepository;
    private final CouponProducer couponProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onOrderRequest(CouponEvent.UsedCoupon event) {
        outboxCouponRepository.save(OutboxCoupon.builder().issuedCouponId(event.command().issuedCouponId()).status(OutboxOrderStatus.PENDING).build());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void success(CouponEvent.UsedCoupon event) {
        couponProducer.request(event);
    }

}
