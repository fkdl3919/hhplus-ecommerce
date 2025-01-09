package kr.hhplus.be.server.domain.order.enums;

public enum OrderStatus {
    PENDING,       // 주문 대기 중 (초기 상태)
    CONFIRMED,     // 주문 완료
    CANCELLED,     // 주문 취소됨
}
