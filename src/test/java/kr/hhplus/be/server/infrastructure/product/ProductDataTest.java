package kr.hhplus.be.server.infrastructure.product;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repository.PointRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.infrastructure.jpa.product.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class ProductDataTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    void setUp(int count) {
        productJpaRepository.deleteAll();

        for (int i = 0; i < count; i++) {
            productJpaRepository.save(
                Product.builder()
                    .name("product" + (i + 1))
                    .price(1000L * (i + 1))
                    .stock(10)
                    .build()
            );
        }
    }

    /**
     * 상품 목록 조회 테스트
     * - 페이징 처리가 올바른지 테스트
     */
    @Test
    @DisplayName("case - 상품 목록 조회 시 페이징 처리가 올바른지 테스트")
    public void productsTest1(){
        // given
        int setUpCount = 25;
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) setUpCount / pageSize);

        // setUpCount 수 만큼 insert
        setUp(setUpCount);

        // when
        for (int page = 0; page < totalPages; page++) {
            int start = page * pageSize;
            int end = Math.min(start + pageSize, setUpCount);

            // 각 페이지 별 아이템 수 기대값
            int itemsPerPage = end - start;

            // page 별 pageable 생성
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Product> products = productRepository.selectProductList(pageable);

            // then
            // 조회 시 페이지별 수량 비교
            assertEquals(itemsPerPage, products.getNumberOfElements());
        }

    }

}
