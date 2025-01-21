package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.command.PaymentCommand;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;


    @Transactional
    public Payment pay(PaymentCommand.Pay command) {
        Payment payment = Payment.builder()
            .user(User.builder().id(command.userId()).build())
            .order(Order.builder().id(command.orderId()).build())
            .payPrice(command.orderPrice() - (command.orderPrice() * command.discountRate() / 100))
            // 결제상태 pending 상태로 진입
            .status(PaymentStatus.PENDING).build();

        // 결제 성공
        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setPaidAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}
