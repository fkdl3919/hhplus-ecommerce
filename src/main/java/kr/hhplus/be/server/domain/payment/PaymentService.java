package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.command.PaymentCommand;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.user.Point;
import kr.hhplus.be.server.domain.user.PointHistory;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repository.PointHistoryRepository;
import kr.hhplus.be.server.domain.user.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;


    @Transactional
    public Payment pay(PaymentCommand paymentCommand) {
        Payment payment = Payment.builder()
            .user(User.builder().id(paymentCommand.userId()).build())
            .order(Order.builder().id(paymentCommand.orderId()).build())
            .originalPrice(paymentCommand.originalPrice())
            .payPrice(paymentCommand.payPrice())
            // 결제상태 pending 상태로 진입
            .status(PaymentStatus.PENDING).build();

        // 결제 성공
        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setPaidAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}
