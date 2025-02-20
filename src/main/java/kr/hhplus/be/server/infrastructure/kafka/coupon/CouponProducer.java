package kr.hhplus.be.server.infrastructure.kafka.coupon;

import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.coupon.used}")
    private String requestTopic;

    public void request(CouponEvent.UsedCoupon event) {
        kafkaTemplate.send(requestTopic, event);
    }

}
