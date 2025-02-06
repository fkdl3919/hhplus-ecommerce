package kr.hhplus.be.server.infrastructure.redis;

import java.util.Collection;
import java.util.List;
import kr.hhplus.be.server.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

@SpringBootTest
public class RedisDataTest {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * RedissonClient
     */
    @Test
    @DisplayName("RedissonClient 데이터 저장 및 직렬화 역직렬화 테스트")
    public void test1() {
        // given
        RScoredSortedSet<Product> test = redissonClient.getScoredSortedSet("test");
        test.add(1,Product.builder().name("test1").build());
        test.add(2,Product.builder().name("test2").build());

        // when
        RScoredSortedSet<Product> test1 = redissonClient.getScoredSortedSet("test");

        // then
        assert test1.size() == 2;
        assert test1.readAll() instanceof List<Product>;
    }

    /**
     * RedisTemplate
     */
    @Test
    @DisplayName("RedisTemplate 데이터 저장 및 직렬화 역직렬화 테스트")
    public void test2() {
        // given
        final String KEY = "test";
        redisTemplate.opsForZSet().add(KEY, Product.builder().name("test1").build(), 1);
        redisTemplate.opsForZSet().add(KEY, Product.builder().name("test2").build(), 2);

        // when
        Collection<Product> products = redisTemplate.opsForZSet().range(KEY, 0, -1);

        // then
        assert products.size() == 2;
    }


}
