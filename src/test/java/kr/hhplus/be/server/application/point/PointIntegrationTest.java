package kr.hhplus.be.server.application.point;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    @DisplayName("동시성 - 다수 요청 포인트 충전")
    public void test() throws Exception {
        // given
        int threadCount = 2;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        Long userId = 1L;
        Long chargePoint = 1000L;
        setUp(userId);

        Charge charge = Charge.builder()
            .userId(userId)
            .point(chargePoint)
            .build();


        // when
        for (int i = 0; i < threadCount; i++) {
            try {
                executorService.submit(
                    () -> {
                        pointService.chargePoint(charge);
                        countDownLatch.countDown();
                    }
                );
            } finally {
            }
        }

        countDownLatch.await();

        // then
        Long point = pointService.userPoint(userId);
        assertEquals(threadCount * chargePoint, point);
    }

}
