package kr.hhplus.be.server.domain.product;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.product.command.ProductCommand;
import kr.hhplus.be.server.domain.product.info.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final RedissonClient redissonClient;

    private final CacheManager cacheManagera;

    @Transactional
    public Product findProductWithLock(long id) {
        return productRepository.findProductWithLock(id).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
    }

    public PageImpl<ProductInfo> selectProductList(Pageable pageable) {
        return ProductInfo.toPaging(productRepository.selectProductPaging(pageable));
    }

    /**
     * 인기 상품 목록 조회
     * 캐시 전략 Cache Aside 사용
     *
     * Cache Aside 전략 사용 이유
     * - 첫번 째 요청 시 DB 조회 후 캐시에 저장
     * - 두번 째 요청부터는 캐시에서 데이터를 가져오기 때문에 DB 조회를 하지 않아도 됨
     * - 캐시 갱신 시점에 DB 조회를 하기 때문에 데이터 일관성이 유지됨
     * - 캐시가 항상 DB의 최신 상태를 반영하도록 강제하지 않기 때문에 성능이 향상됨
     *
     * 캐시 스탬피드 현상을 고려하여 Redisson을 사용하여 분산 락을 적용하여 하나의 요청만 DB 조회하도록 함
     *
     * @return List<ProductInfo> productInfoList
     */
    public List<ProductInfo> selectTopSellingProductList() {
        final String REDISSON_LOCK_PREFIX = "LOCK:";
        final String CACHE_NAME = "product";
        final String CACHE_KEY = "topSellingProductList";
        // 락 대기 시간
        final long LOCK_WAIT_TIME = 5L;
        // 락 유지 시간
        final long LOCK_LEASE_TIME = 10L;

        // 캐시매니저에서 캐시 가져오기
        Cache cache = cacheManagera.getCache(CACHE_NAME);

        // 캐시 조회
        List<Product> products = cache.get(CACHE_KEY, List.class);

        if(products != null) {
            // 캐시 히트 - 캐시에서 데이터 가져오기
            return ProductInfo.toInfos(products);
        }

        // 캐시 미스인 경우 분산 락 적용하여 하나의 요청만 DB 조회하도록 함
        RLock rLock = redissonClient.getLock(REDISSON_LOCK_PREFIX + CACHE_NAME + ":" + CACHE_KEY);

        try {
            // 하나의 요청이 락을 획득
            boolean available = rLock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!available) {
                throw new RuntimeException("상품 목록 조회 중 오류가 발생하였습니다.");
            }

            // 락을 획득한 후 다시 캐시를 조회하여 데이터가 있는지 확인 (다른 스레드가 이미 캐시를 갱신한 경우)
            products = cache.get(CACHE_KEY, List.class);
            if(products != null) {
                return ProductInfo.toInfos(products);
            }

            // DB 조회
            products = productRepository.selectTopSellingProducts();
            cache.put(CACHE_KEY, products);

            return ProductInfo.toInfos(products);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("상품 목록 조회 중 오류가 발생하였습니다.");
        } finally {
            try {
                rLock.unlock();
                log.info("Redisson UnLocked {}", CACHE_KEY);
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock {}", CACHE_KEY);
            }
        }
    }

    public List<Product> getProducts(List<ProductCommand.Get> commands){
        List<Product> collect = commands.stream().map(
            (command) -> productRepository.findById(command.productId()).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."))
        ).collect(Collectors.toList());
        return collect;
    }

    public Long getAmountOfProducts(List<ProductCommand.GetAmount> commands){
        return commands.stream().mapToLong(
            (command) -> {
                Product product = productRepository.findById(command.productId()).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
                return product.getPrice() * command.quantity();
            }
        ).sum();
    }

    // 재고 차감시 lock 사용
    public void deduct(List<ProductCommand.Deduct> deducts ) {
        deducts.forEach(deduct -> {
            Product product = productRepository.findProductWithLock(deduct.productId()).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
            product.decrementStock(deduct.quantity());
        });
    }
}
