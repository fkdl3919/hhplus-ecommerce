package kr.hhplus.be.server.application.order;

import java.util.List;
import lombok.Getter;

@Getter
public class AmountCalculator {

    private long totalAmount = 0;

    // 상품 별 주문금액을 반환
    // 계산후 totalAmount에 누적
    public long originalAmount(long productPrice, long quantity){
        long amount = productPrice * quantity;
        totalAmount += amount;
        return amount;
    }

    // 할인율을 적용한 주문금액
    public long discountAmount(long discountRate){
        return totalAmount - (totalAmount * discountRate / 100);
    }

}
