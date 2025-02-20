package kr.hhplus.be.server.interfaces.kafka;

import kr.hhplus.be.server.interfaces.event.payment.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.payment-success}")
    private String successTopic;

    @GetMapping("/send")
    public String sendMessage(@RequestParam("msg") String message) {
        // "test-topic" 토픽에 메시지 전송
//        kafkaTemplate.send("test-topic", message);

        PaymentSuccessEvent event = new PaymentSuccessEvent(1L, 2L);
        kafkaTemplate.send(successTopic, event);
        return "Message sent: " + message;
    }

}
