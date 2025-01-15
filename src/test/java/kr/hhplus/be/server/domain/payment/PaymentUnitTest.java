package kr.hhplus.be.server.domain.payment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.command.PaymentCommand;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentUnitTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    /**
     * 결제
     */
    @Test
    @DisplayName("결제 성공시 결제 상태가 CONFIRMED 인지 확인")
    public void payTest(){
        // given
        long userId = 1L;
        long orderId = 1L;
        long originalPrice = 1000L;
        int discountRate = 10;

        PaymentCommand.Pay command = PaymentCommand.Pay.builder()
            .userId(userId)
            .orderId(orderId)
            .orderPrice(originalPrice)
            .discountRate(discountRate)
            .build();

        long payPrice = originalPrice - (originalPrice * discountRate / 100);
        Payment payment = Payment.builder()
            .user(User.builder().id(userId).build())
            .order(Order.builder().id(orderId).build())
            .payPrice(payPrice)
            .status(PaymentStatus.CONFIRMED)
            .build();

        when(paymentRepository.save(any())).thenReturn(payment);

        // when
        Payment pay = paymentService.pay(command);

        // then
        assertEquals(PaymentStatus.CONFIRMED, pay.getStatus());

    }

    @Test
    @DisplayName("결제 시 결제 금액이 올바른지 확인")
    public void payTest2(){
        // given
        long userId = 1L;
        long orderId = 1L;
        long originalPrice = 1000L;
        int discountRate = 10;

        PaymentCommand.Pay command = PaymentCommand.Pay.builder()
            .userId(userId)
            .orderId(orderId)
            .orderPrice(originalPrice)
            .discountRate(discountRate)
            .build();

        long payPrice = originalPrice - (originalPrice * discountRate / 100);
        Payment payment = Payment.builder()
            .user(User.builder().id(userId).build())
            .order(Order.builder().id(orderId).build())
            .payPrice(payPrice)
            .status(PaymentStatus.CONFIRMED)
            .build();

        when(paymentRepository.save(any())).thenReturn(payment);

        // when
        Payment pay = paymentService.pay(command);

        // then
        assertEquals(payPrice, pay.getPayPrice());

    }


}
