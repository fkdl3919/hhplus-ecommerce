package kr.hhplus.be.server.domain.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class ProductCacheTest {

    @Autowired
    private ProductService productService;

    @MockitoBean
    private ProductRepository productRepository;


    /**
     * 인기상품 목록 조회
     * 캐시 적용
     */
    @Test
    @DisplayName("case - 인기상품 목록 조회 시 첫 번째 요청인 경우 DB 한번만 조회")
    public void test5(){
        // given
        when(productRepository.selectTopSellingProducts()).thenReturn(Arrays.asList(Product.builder().build()));

        // when
        productService.selectTopSellingProductList();
        productService.selectTopSellingProductList();
        productService.selectTopSellingProductList();

        // then
        verify(productRepository, times(1)).selectTopSellingProducts();
    }



}
