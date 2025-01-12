package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import kr.hhplus.be.server.domain.coupon.info.IssuedCouponInfo;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Transactional
    public IssuedCoupon issueCoupon(long couponId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));

        Coupon coupon = couponRepository.findByIdWithLock(couponId).orElseThrow(() -> new EntityNotFoundException("쿠폰이 존재하지 않습니다."));

        // 쿠폰 수량체크
        coupon.validAvailable();

        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
            .coupon(coupon)
            .user(user)
            .status(CouponStatus.NOT_USED)
            // 만료일자는 7일 뒤
            .expireDate(LocalDate.now().plus(7, ChronoUnit.DAYS))
            .build();

        // 유저와 쿠폰 매핑한 데이터 저장
        couponRepository.saveIssuedCoupon(issuedCoupon);

        if(issuedCoupon.getId() == null){
            throw new IllegalArgumentException("쿠폰을 입력하는 도중 오류가 발생하였습니다.");
        }

        // 쿠폰 수량차감
        coupon.decreaseStock();

        return issuedCoupon;

    }

    public PageImpl<IssuedCouponInfo> selectIssuedCouponList(long userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));

        return IssuedCouponInfo.toPaging(couponRepository.selectIssuedCouponList(userId, pageable));
    }

    /**
     * 발급쿠폰 유효기간 검증 후 쿠폰의 할인율 반환
     * @param issuedCouponId
     * @return
     */
    public long getDiscountRate(long issuedCouponId) {
        IssuedCoupon issuedCoupon = couponRepository.findIssuedCouponById(issuedCouponId).orElseThrow(() -> new EntityNotFoundException("보유쿠폰이 존재하지 않습니다."));
        issuedCoupon.validCouponExpired();
        return issuedCoupon.getCoupon().getDiscountRate();
    }
}
