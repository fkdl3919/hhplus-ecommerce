package kr.hhplus.be.server.infrastructure.kafka.payment;

import kr.hhplus.be.server.domain.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.payment.success}")
    private String successTopic;

    public void success(PaymentSuccessEvent event) {
        kafkaTemplate.send(successTopic, event);
    }

}
