package kr.hhplus.be.server.domain.payment.enums;

public enum OutboxPaymentStatus {
    PENDING,       // 대기 중 (초기 상태)
    CONFIRMED,     // 완료
    CANCELLED,     // 취소됨
}
