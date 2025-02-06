package kr.hhplus.be.server.infrastructure.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void givenRedisContainerConfiguredWithDynamicProperties_whenCheckingRunningStatus_thenStatusIsRunning() {
        Assertions.assertTrue(redissonClient.getBucket("test").trySet("test"));

    }

}
