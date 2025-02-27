package kr.hhplus.be.server.domain.product.enums;

public enum OutboxProductStatus {
    PENDING,       // 대기 중 (초기 상태)
    CONFIRMED,     // 완료
    CANCELLED,     // 취소됨
}
