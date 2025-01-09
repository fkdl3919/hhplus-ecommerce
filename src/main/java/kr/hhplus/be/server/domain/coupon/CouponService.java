package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserRepository userRepository;

    @Transactional
    public void issueCoupon(long id, long userId) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));

        Coupon coupon = couponRepository.findByIdWithLock(id).orElseThrow(() -> new EntityNotFoundException("쿠폰이 존재하지 않습니다."));

        // 쿠폰 수량체크
        coupon.validAvailable();

        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
            .coupon(coupon)
            .user(user)
            .status(CouponStatus.NOT_USED)
            .build();

        // 유저와 쿠폰 매핑한 데이터 저장
        issuedCouponRepository.save(issuedCoupon);

        if(issuedCoupon.getId() == null){
            throw new IllegalArgumentException("쿠폰을 입력하는 도중 오류가 발생하였습니다.");
        }

        // 쿠폰 수량차감
        coupon.decreaseStock();

    }

}
