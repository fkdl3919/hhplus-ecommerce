package kr.hhplus.be.server.application.product;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private OrderRepository orderRepository;




    public User setUpUser() {
        return userRepository.save(User.builder().build());
    }

    public Product setUpProduct(int stock, long price, String name) {
        return productRepository.save(Product.builder().price(price).name(name).stock(stock).build());
    }

    public Order setUpOrder(long userId, long productId, int orderQuantity) {
        Order order = Order.builder()
            .userId(userId)
            .build();
        order.confirmOrder();

        OrderItem orderItem = OrderItem.builder()
            .order(order)
            .productId(productId)
            .quantity(orderQuantity).build();

        Order save = orderRepository.save(order);

        orderRepository.saveOrderItem(orderItem);

        return save;
    }

    /**
     * 인기상품 조회
     */
    @Test
    @DisplayName("case - 최근 3일간 가장 많이 팔린 상위 5개 상품정보를 조회")
    public void topSellerTest1(){
        // given
        User user = setUpUser();

        // setup 상품 가격 100
        final long productPrice = 100;

        // setup 상품 수량
        final int productStock = 1000;

        // setup 상품
        String productName = "선풍기";
        String productName2 = "세탁기";
        String productName3 = "컴퓨터";
        String productName4 = "건조기";
        String productName5 = "전자레인지";
        String productName6 = "청소기";
        String productName7 = "스타일러";

        Product product = setUpProduct(productStock, productPrice, productName);
        Product product2 = setUpProduct(productStock, productPrice, productName2);
        Product product3 = setUpProduct(productStock, productPrice, productName3);
        Product product4 = setUpProduct(productStock, productPrice, productName4);
        Product product5 = setUpProduct(productStock, productPrice, productName5);
        Product product6 = setUpProduct(productStock, productPrice, productName6);
        Product product7 = setUpProduct(productStock, productPrice, productName7);

        // setup 주문 - 선풍기
        setUpOrder(user.getId(), product.getId(), 100);

        // setup 주문 - 세탁기
        setUpOrder(user.getId(), product2.getId(), 200);

        // setup 주문 - 컴퓨터
        setUpOrder(user.getId(), product3.getId(), 300);

        // setup 주문 - 건조기
        setUpOrder(user.getId(), product4.getId(), 400);

        // setup 주문 - 전자레인지
        setUpOrder(user.getId(), product5.getId(), 500);

        // setup 주문 - 청소기
        setUpOrder(user.getId(), product6.getId(), 600);

        // setup 주문 - 스타일러
        setUpOrder(user.getId(), product7.getId(), 700);


        // when
        List<Product> products = productRepository.selectTopSellingProducts();

        // then
        int limit = 5;
        assertEquals(limit, products.size());
        assertTrue(products.stream().anyMatch((selectProduct) -> productName7.equals(selectProduct.getName())));
        assertTrue(products.stream().anyMatch((selectProduct) -> productName6.equals(selectProduct.getName())));
        assertTrue(products.stream().anyMatch((selectProduct) -> productName5.equals(selectProduct.getName())));
        assertTrue(products.stream().anyMatch((selectProduct) -> productName4.equals(selectProduct.getName())));
        assertTrue(products.stream().anyMatch((selectProduct) -> productName3.equals(selectProduct.getName())));

    }

}
