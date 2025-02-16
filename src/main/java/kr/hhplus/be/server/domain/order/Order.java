package kr.hhplus.be.server.domain.order;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.common.Base;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@Table(name = "order_t")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "order_t"
//    , indexes = {
//    @Index(name = "idx_status_ordered_at", columnList = "status, orderedAt")
//}
)
public class Order extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long issuedCouponId;

    // 주문 완료시간
    private LocalDateTime orderedAt;

    // 주문 금액
    private Long orderPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public void confirmOrder(){
        this.status = OrderStatus.CONFIRMED;
        this.orderedAt = LocalDateTime.now();
    }

}
