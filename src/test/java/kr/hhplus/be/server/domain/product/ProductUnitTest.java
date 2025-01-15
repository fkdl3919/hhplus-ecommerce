package kr.hhplus.be.server.domain.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductUnitTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    /**
     * 주문을 위한 상품 검증 테스트
     */
    @Test
    @DisplayName("case - 상품이 품절된 경우 IllegalArgumentException 발생")
    public void test(){
        // given
        Product product = Product.builder()
            .stock(0)
            .build();

        int requestStock = 5;
        long userPoint = 1000;

        // when
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
           product.decrementStock(requestStock);
        });

        // then
        assertEquals("상품이 품절되었습니다.", illegalArgumentException.getMessage());

    }

    @Test
    @DisplayName("case - 요청 수량이 재고수량을 초과한 경우 IllegalArgumentException 발생")
    public void test2(){
        // given
        Product product = Product.builder()
            .stock(3)
            .build();

        int requestStock = 5;
        long userPoint = 1000;

        // when
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            product.decrementStock(requestStock);
        });

        // then
        assertEquals("요청하신 수량이 상품 재고수량을 초과하였습니다.", illegalArgumentException.getMessage());

    }

    @Test
    @DisplayName("case - 요청 수량이 재고수량을 초과한 경우 IllegalArgumentException 발생")
    public void test3(){
        // given
        Product product = Product.builder()
            .stock(3)
            .price(2000L)
            .build();

        int requestStock = 5;
        long userPoint = 1000;

        // when
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            product.decrementStock(requestStock);
        });

        // then
        assertEquals("요청하신 수량이 상품 재고수량을 초과하였습니다.", illegalArgumentException.getMessage());

    }

    /**
     * 상품 조회 시
     */
    @Test
    @DisplayName("case - 상품이 존재하지 않는 경우")
    public void test4(){
        // given
        long productId = 1;
        when(productRepository.findProductWithLock(productId)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            productService.findProductWithLock(productId);
        });

        // then
        assertEquals("상품이 존재하지 않습니다.", entityNotFoundException.getMessage());

    }


}
