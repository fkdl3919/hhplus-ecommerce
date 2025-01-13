package kr.hhplus.be.server.domain.payment.repository;

import kr.hhplus.be.server.domain.payment.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment findPayMentByOrderId(Long orderId);
}
