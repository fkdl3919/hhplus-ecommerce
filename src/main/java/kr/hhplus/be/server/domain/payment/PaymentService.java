package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.user.Point;
import kr.hhplus.be.server.domain.user.PointHistory;
import kr.hhplus.be.server.domain.user.repository.PointHistoryRepository;
import kr.hhplus.be.server.domain.user.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PointHistoryRepository pointHistoryRepository;


    @Transactional
    public Payment pay(Payment payment, Point userPoint) {
        // 결제상태 pending 상태로 진입

        // 결제 성공
        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setPaidAt(LocalDateTime.now());

        // 잔액 차감
        PointHistory pointHistory = userPoint.use(payment.getAmount());

        // 포인트 내역 입력
        pointHistoryRepository.save(pointHistory);

        return paymentRepository.save(payment);
    }
}
