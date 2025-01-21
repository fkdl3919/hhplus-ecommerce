package kr.hhplus.be.server.domain.point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import kr.hhplus.be.server.domain.point.command.PointCommand;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PointUnitTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Test
    @DisplayName("case - 유저의 포인트가 존재하지 않을 경우 포인트가 0인 포인트 객체 반환")
    public void userPointTest2(){
        // given
        long userId = 1L;

        User user = User.builder().id(userId).build();

        when(pointRepository.findPointByUserIdWithLock(user.getId()))
            .thenReturn(Optional.of(new Point()));

        // when
        Long point = pointService.userPoint(userId);

        // then
        assertEquals(0, point);

    }

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
            .userId(userId)
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
            .userId(userId)
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
            .userId(userId)
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

        PointCommand.Charge command = PointCommand.Charge
            .builder()
            .userId(userId)
            .point(chargeAmount)
            .build();

        Point point = Point.builder()
            .userId(userId)
            .point(userPoint)
            .build();

        User user = User.builder()
            .id(userId)
            .build();

        when(pointRepository.findPointByUserIdWithLock(userId)).thenReturn(Optional.of(point));

        // when
        pointService.chargePoint(command);

        // then
        assertEquals(userPoint + chargeAmount, point.getPoint());
        verify(pointRepository, times(1)).saveHistory(any());
    }


}
