package kr.hhplus.be.server.domain.point;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import kr.hhplus.be.server.domain.common.Base;
import kr.hhplus.be.server.domain.point.enums.PointTransactionType;
import kr.hhplus.be.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
    @Index(name = "idx_user_id", columnList = "userId")
})
public class Point extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Builder.Default()
    private Long point = 0L;

    @Version
    private Long version;

    public PointHistory charge(Long amount) {
        this.point += amount;

        // 충전 후 포인트 히스토리 테이블 입력
        PointHistory pointHistory = PointHistory.builder()
            .amount(amount)
            .type(PointTransactionType.CHARGE)
            .updatedPoint(this.point)
            .build();
        return pointHistory;
    }

    public PointHistory use(Long amount) {
        if (amount > this.point) throw new IllegalArgumentException("잔액이 부족합니다");
        this.point -= amount;

        // 사용 후 포인트 히스토리 테이블 입력
        PointHistory pointHistory = PointHistory.builder()
            .amount(amount)
            .type(PointTransactionType.USE)
            .updatedPoint(this.point)
            .build();
        return pointHistory;
    }
}
