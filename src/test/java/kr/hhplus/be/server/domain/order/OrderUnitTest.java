package kr.hhplus.be.server.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.command.OrderCommand.OrderItemCommand;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.collect.Lists;

@ExtendWith(MockitoExtension.class)
public class OrderUnitTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    /**
     * 주문 생성
     */
    @Test
    @DisplayName("case - 주문 생성이 되지않았다면 EntityNotFoundException 발생")
    public void orderTest(){
        // given
        long userId = 1L;
        long issuedCouponId = 1L;

        // 주문생성 command
        OrderCommand orderCommand = new OrderCommand(
            userId,
            issuedCouponId,
            List.of(
                new OrderItemCommand(1L, 10),
                new OrderItemCommand(2L, 20),
                new OrderItemCommand(3L, 30)
            )
        );

        // 주문
        Order order = Order.builder()
            .user(User.builder().id(orderCommand.userId()).build())
            .status(OrderStatus.PENDING)
            .issuedCoupon(IssuedCoupon.builder().id(orderCommand.issuedCouponId()).build())
            .build();

        when(orderRepository.save(any())).thenReturn(order);

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> orderService.order(orderCommand));

        // then
        assertEquals("주문이 생성되지 않았습니다.", exception.getMessage());

    }

    @Test
    @DisplayName("case - 주문 생성 후 주문별 아이템 수 만큼 주문아이템이 입력되는지 확인")
    public void orderTest2(){
        // given
        long userId = 1L;
        long issuedCouponId = 1L;
        long orderId = 1L;

        // 주문생성 command
        OrderCommand orderCommand = new OrderCommand(
            userId,
            issuedCouponId,
            List.of(
                new OrderItemCommand(1L, 10),
                new OrderItemCommand(2L, 20),
                new OrderItemCommand(3L, 30)
            )
        );

        // 주문
        Order order = Order.builder()
            .id(orderId)
            .user(User.builder().id(orderCommand.userId()).build())
            .status(OrderStatus.PENDING)
            .issuedCoupon(IssuedCoupon.builder().id(orderCommand.issuedCouponId()).build())
            .build();

        when(orderRepository.save(any())).thenReturn(order);

        // when
        orderService.order(orderCommand);

        // then
        verify(orderRepository,times(orderCommand.products().size())).saveOrderItem(any());

    }



}
