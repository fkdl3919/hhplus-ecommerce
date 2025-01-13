package kr.hhplus.be.server.domain.user;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.domain.common.Base;
import kr.hhplus.be.server.domain.user.enums.PointTransactionType;
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
public class Point extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    private Long point = 0L;

    // 포인트 객체가 없을 시 포인트가 0인 객체를 반환
    public static Point emptyPoint(User user) {
        Point point = new Point();
        point.user = user;
        return point;
    }

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
