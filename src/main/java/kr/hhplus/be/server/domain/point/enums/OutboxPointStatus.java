package kr.hhplus.be.server.domain.point.enums;

public enum OutboxPointStatus {
    PENDING,       // 대기 중 (초기 상태)
    CONFIRMED,     // 완료
    CANCELLED,     // 취소됨
}
