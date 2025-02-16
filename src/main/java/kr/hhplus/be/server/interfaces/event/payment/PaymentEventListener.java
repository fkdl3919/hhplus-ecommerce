package kr.hhplus.be.server.interfaces.event.payment;

import kr.hhplus.be.server.infrastructure.dataplatform.Dataplatform;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEventListener {


    // 비동기로 이벤트 발행주체의 트랜잭션이 커밋된 후에 수행한다.
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
        // (4) 주문 정보 전달
        Dataplatform.sendData(event.getOrderKey());
    }

}
