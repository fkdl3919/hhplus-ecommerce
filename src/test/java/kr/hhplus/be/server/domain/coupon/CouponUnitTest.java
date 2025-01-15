package kr.hhplus.be.server.domain.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CouponUnitTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    /**
     * 쿠폰 발급
     */
    @Test
    @DisplayName("case - 쿠폰 발급시 유저가 존재하지 않는다면 EntityNotFoundException 발생")
    public void issuedCouponTest1(){
        // given
        long userId = 1L;
        long couponId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> couponService.issueCoupon(couponId, userId));

        // then
        assertEquals("유저가 존재하지 않습니다.", exception.getMessage());

    }

    @Test
    @DisplayName("case - 쿠폰 발급시 쿠폰이 존재하지 않는다면 EntityNotFoundException 발생")
    public void issuedCouponTest2(){
        // given
        long userId = 1L;
        long couponId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(couponRepository.findByIdWithLock(couponId)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> couponService.issueCoupon(couponId, userId));

        // then
        assertEquals("쿠폰이 존재하지 않습니다.", exception.getMessage());

    }

    @Test
    @DisplayName("case - 쿠폰 발급시 쿠폰 수량이 모두 소진되었다면 IllegalArgumentException 발생")
    public void issuedCouponTest3(){
        // given
        long userId = 1L;
        long couponId = 1L;

        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Coupon coupon = Coupon.builder().id(couponId).stock(0).build();
        when(couponRepository.findByIdWithLock(couponId)).thenReturn(Optional.of(coupon));

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> couponService.issueCoupon(couponId, userId));

        // then
        assertEquals("쿠폰이 모두 소진되었습니다.", exception.getMessage());

    }

    @Test
    @DisplayName("case - 쿠폰 발급시 유저와 쿠폰 매핑 데이터가 저장되지 않았다면 IllegalArgumentException 발생")
    public void issuedCouponTest4(){
        // given
        long userId = 1L;
        long couponId = 1L;

        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Coupon coupon = Coupon.builder().id(couponId).stock(10).build();
        when(couponRepository.findByIdWithLock(couponId)).thenReturn(Optional.of(coupon));

        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
            .coupon(coupon)
            .user(user)
            .status(CouponStatus.NOT_USED)
            .build();

        when(couponRepository.saveIssuedCoupon(any())).thenReturn(issuedCoupon);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> couponService.issueCoupon(couponId, userId));

        // then
        assertEquals("쿠폰을 입력하는 도중 오류가 발생하였습니다.", exception.getMessage());

    }






}
