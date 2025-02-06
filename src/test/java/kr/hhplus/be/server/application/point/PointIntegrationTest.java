package kr.hhplus.be.server.application.point;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.command.PointCommand.Charge;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class PointIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    public void setUp(long userId) throws Exception {
        Point point = new Point();
        point.setUserId(userId);
        pointRepository.save(point);
    }


    @Test
    @DisplayName("동시성 - 낙관적 락 적용 다수 요청 포인트 충전 시 ObjectOptimisticLockingFailureException 발생")
    public void test() throws Exception {
        // given
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        Long userId = 1L;
        Long chargePoint = 1000L;
        setUp(userId);

        Charge charge = Charge.builder()
            .userId(userId)
            .point(chargePoint)
            .build();

        AtomicReference<Exception> e = new AtomicReference<>();
        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(
                () -> {
                    try {
                        ObjectOptimisticLockingFailureException objFailure = assertThrows(ObjectOptimisticLockingFailureException.class, () -> pointService.chargePoint(charge));
                        e.set(objFailure);
                    }finally{
                        countDownLatch.countDown();
                    }

                }
            );
        }

        countDownLatch.await();

        // then
        assertTrue(e.get() instanceof ObjectOptimisticLockingFailureException);
    }

}
