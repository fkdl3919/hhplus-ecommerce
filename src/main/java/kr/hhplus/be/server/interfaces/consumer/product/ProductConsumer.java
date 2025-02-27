package kr.hhplus.be.server.interfaces.consumer.product;

import jakarta.persistence.EntityNotFoundException;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.order.enums.OutboxOrderStatus;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.order.outbox.OutboxOrderRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.command.ProductCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductConsumer {

    private final OutboxOrderRepository outboxOrderRepository;
    private final ProductService productService;

    @KafkaListener(topics = "${spring.kafka.topic.order.request}", groupId = "product.consumer")
    public void consumeOrderRequest(OrderEvent.OrderRequest event) {
        productService.deduct(
            event.command().products().stream().map((orderItemCommand) -> ProductCommand.Deduct.builder().productId(orderItemCommand.productId()).quantity(orderItemCommand.quantity()).build()).collect(Collectors.toList())
        );
    }

}
