package kr.hhplus.be.server.infrastructure.jpa.point;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.jpa.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

@Slf4j
@SpringBootTest
public class PointDataTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Transactional
    public void setUp(int count) {
        final int batchSize = 10000; // 배치 사이즈 설정

        for (int i = 0; i < count; i++) {
            User user = User.builder()
                .name("user" + (i + 1))
                .build();

            Point build = Point.builder()
                .userId(user.getId())
                .point(1000L * (i + 1))
                .build();

            entityManager.persist(user);
            entityManager.persist(build);

            if (i % (batchSize / 2) == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        System.out.println("데이터 생성 완료");
    }

    /**
     * 포인트 테이블의 인덱스 사용 테스트
     * create index idx_user_id on point(user_id);
     */
    @Test
    @Transactional
    @DisplayName("포인트 조회 테스트 - 인덱스 사용 테스트")
    public void test1(){
        // given
        setUp(300000);

        String sql = "explain select * from point a where a.user_id = ?";
        Query query = entityManager.createNativeQuery(sql);
        int o = (int) (Math.random() * 300000) + 1;
        query.setParameter(1, o);

        // when
        // 실행계획 출력
        List<Object[]> results = query.getResultList();

        results.forEach(result -> {
            log.info(Arrays.toString(result));
        });

        // then
        boolean indexUsed = results.stream()
            .anyMatch(result -> Arrays.stream(result).anyMatch( r -> "idx_user_id".equals(r)));

        assertTrue(indexUsed);
    }

    /**
     * 포인트 테이블의 인덱스를 사용하지 않는지 테스트
     * create index idx_user_id on point(user_id);
     */
    @Test
    @Transactional
    @DisplayName("포인트 조회 테스트 - 인덱스 미사용 테스트")
    public void test2(){
        String dropIndex = "drop index idx_user_id on point";

        entityManager.createNativeQuery(dropIndex).executeUpdate();

        // given
        setUp(300000);

        String sql = "explain select * from point a where a.user_id = ?";
        Query query = entityManager.createNativeQuery(sql);
        int o = (int) (Math.random() * 300000) + 1;

        query.setParameter(1, o);

        // when
        // 실행계획 출력
        List<Object[]> results = query.getResultList();

        results.forEach(result -> {
            log.info(Arrays.toString(result));
        });

        // then
        boolean indexUsed = results.stream()
            .anyMatch(result -> Arrays.stream(result).anyMatch( r -> "idx_user_id".equals(r)));

        assertFalse(indexUsed);
    }

    /**
     * 60 milliseconds
     */
    @Test
    @Transactional
    @DisplayName("포인트 조회 테스트 - 인덱스 성능 테스트 사용 시")
    public void test3(){
        // given
        setUp(300000);
        int o = 226670;

        // when
        long startTime = System.currentTimeMillis();
        pointJpaRepository.findByUserIdWithVersion(o);
        long endTime = System.currentTimeMillis();

        log.info("실행시간 : {} milliseconds", endTime - startTime);

    }

    /**
     * 112 milliseconds
     */
    @Test
    @Transactional
    @DisplayName("포인트 조회 테스트 - 인덱스 성능 테스트 미사용 시")
    public void test4(){
        String dropIndex = "drop index idx_user_id on point";

        entityManager.createNativeQuery(dropIndex).executeUpdate();

        // given
        setUp(300000);
        int o = 226670;
        // when
        long startTime = System.currentTimeMillis();
        pointJpaRepository.findByUserIdWithVersion(o);
        long endTime = System.currentTimeMillis();

        log.info("실행시간 : {} milliseconds", endTime - startTime);
    }



}
