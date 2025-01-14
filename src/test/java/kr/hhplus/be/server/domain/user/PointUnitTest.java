package kr.hhplus.be.server.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import java.util.Optional;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.repository.PointRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PointUnitTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointRepository pointRepository;


    /**
     * 포인트 충전
     */
    @Test
    @DisplayName("case - 포인트 충전 후 정상적으로 충전되었는지 테스트")
    public void chargePointTest(){
        // given
        long userId = 1L;
        long userPoint = 2000L;

        long chargeAmount = 1000L;

        Point point = Point.builder()
            .user(User.builder().id(userId).build())
            .point(userPoint)
            .build();

        // when
        point.charge(chargeAmount);

        // then
        assertEquals(userPoint + chargeAmount, point.getPoint());
    }

    /**
     * 포인트 사용
     */
    @Test
    @DisplayName("case - 포인트 사용 시 잔액이 부족하면 IllegalArgumentException 발생")
    public void usePointUseTest(){
        // given
        long userId = 1L;
        long userPoint = 2000L;

        long useAmount = 3000L;

        Point point = Point.builder()
            .user(User.builder().id(userId).build())
            .point(userPoint)
            .build();

        // when
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> point.use(useAmount));

        // then
        assertEquals("잔액이 부족합니다", illegalArgumentException.getMessage());
    }

    @Test
    @DisplayName("case - 포인트 사용 시 정상적으로 사용되었는지 테스트")
    public void usePointUseTest2(){
        // given
        long userId = 1L;
        long userPoint = 4000L;

        long useAmount = 3000L;

        Point point = Point.builder()
            .user(User.builder().id(userId).build())
            .point(userPoint)
            .build();

        // when
        point.use(useAmount);

        // then
        assertEquals(userPoint - useAmount, point.getPoint());
    }

    @Test
    @DisplayName("case - 포인트 충전 후 충전금액이 정확하고 히스토리 저장이 호출되었는지 테스트")
    public void usePointChargeTest1(){
        // given
        long userId = 1L;
        long userPoint = 4000L;

        long chargeAmount = 3000L;

        Point point = Point.builder()
            .user(User.builder().id(userId).build())
            .point(userPoint)
            .build();

        User user = User.builder()
            .id(userId)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointRepository.findPointByUserIdWithLock(userId)).thenReturn(Optional.of(point));

        // when
        userService.chargePoint(userId, chargeAmount);


        // then
        assertEquals(userPoint + chargeAmount, point.getPoint());
        verify(pointRepository, times(1)).saveHistory(any());
    }


}
