package kr.hhplus.be.server.domain.payment.command;

import java.util.Objects;
import lombok.Builder;

public class PaymentCommand {

    @Builder
    public record Pay(
        Long userId,
        Long orderId,
        Long originalPrice,
        Integer discountRate
    ){

        public Pay {
            Objects.requireNonNullElseGet(discountRate, () -> 0);
        }
    }

}
