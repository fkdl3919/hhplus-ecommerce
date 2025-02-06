package kr.hhplus.be.server.infrastructure.redis;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedissonClient redissonClient;

    public boolean setSortedSet(String key, String value) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(key);
        return sortedSet.add(System.currentTimeMillis(), value);
    }

    public List<String> getSortedSetValues(String key, int startIndex, int endIndex) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(key);
        Collection<String> strings = sortedSet.valueRange(startIndex, endIndex);
        return strings.stream().collect(Collectors.toList());
    }

    public int getSortedSetSize(String key) {
        return redissonClient.getScoredSortedSet(key).size();
    }

    public int getSetSize(String key) {
        return redissonClient.getSet(key).size();
    }


    // 주어진 pattern에 매칭되는 모든 key를 반환
    public Iterable<String> getAllKeys(String pattern) {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> foundedKeys = keys.getKeys(KeysScanOptions.defaults().pattern(pattern));
        return foundedKeys;
    }

    public void deleteValue(String key, String value) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(key);
        sortedSet.remove(value);
    }

    public boolean setSet(String key, String value) {
        return redissonClient.getSet(key).add(value);
    }

    public Optional<String> getSetValue(String key, String value) {
        // java Set은 contains 메소드로 해당 값이 존재하는지 확인 가능
        return redissonClient.getSet(key).contains(value) ? Optional.of(key) : Optional.empty();
    }

}
