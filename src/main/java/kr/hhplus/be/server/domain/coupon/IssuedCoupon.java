package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import kr.hhplus.be.server.domain.common.Base;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssuedCoupon extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDate expireDate;

    public void validCouponExpired() {
        if(this.expireDate == null || this.expireDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("보유하신 쿠폰의 유효기간이 만료되었습니다.");
    }

}
