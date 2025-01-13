package kr.hhplus.be.server.application.order;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AmountCalculatorTest {

    @Test
    @DisplayName("case - 상품 금액 계산기가 올바른 총합을 나타내는지 테스트")
    public void calculatorTest1(){
        // given
        AmountCalculator amountCalculator = new AmountCalculator();

        // 상품별 금액
        final long productPrice = 1000;

        // 주문 상품 수
        final int orderItemSize = 10;
        // 주문 상품 별 수
        final long orderQuantity = 10;


        for (int i = 0; i < orderItemSize; i++) {
             amountCalculator.addAmount(productPrice, orderQuantity);
        }

        Assertions.assertEquals(productPrice * (orderItemSize * orderQuantity), amountCalculator.getTotalAmount());
    }

    @Test
    @DisplayName("case - 상품 금액 계산기가 올바른 총합을 나타내는지 테스트")
    public void calculatorTest2(){
        // given
        AmountCalculator amountCalculator = new AmountCalculator();

        // 상품별 금액
        final long productPrice = 1000;

        // 주문 상품 수
        final int orderItemSize = 10;
        // 주문 상품 별 수
        final long orderQuantity = 10;

        // 할인율
        final int discountRate = 10;

        // when
        for (int i = 0; i < orderItemSize; i++) {
            amountCalculator.addAmount(productPrice, orderQuantity);
        }

        // then
        long originalPrice = productPrice * (orderItemSize * orderQuantity);
        Assertions.assertEquals((originalPrice - (originalPrice * discountRate / 100 ) ), amountCalculator.getDiscountAmount(discountRate));
    }


}
