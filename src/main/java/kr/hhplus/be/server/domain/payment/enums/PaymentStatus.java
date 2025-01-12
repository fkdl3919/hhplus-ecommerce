package kr.hhplus.be.server.domain.payment.enums;

public enum PaymentStatus {
    PENDING,       // 결제 대기 중 (초기 상태)
    CONFIRMED,     // 결제 완료
    CANCELLED,     // 결제 취소됨
}
