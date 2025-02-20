package kr.hhplus.be.server.infrastructure.kafka.order;

import kr.hhplus.be.server.domain.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.order.request}")
    private String requestTopic;

    public void request(OrderEvent.OrderRequest event) {
        kafkaTemplate.send(requestTopic, event);
    }

}
