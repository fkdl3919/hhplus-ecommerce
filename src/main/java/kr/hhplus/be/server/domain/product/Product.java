package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.common.Base;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long price;

    private Integer stock;

    /**
     * 상품 갯수 차감
     *
     */
    public void decrementProductStock(int requestStock, Long userPoint) {
        if(stock == null || stock <= 0) throw new IllegalArgumentException("상품이 품절되었습니다.");

        if(requestStock > stock) throw new IllegalArgumentException("요청하신 수량이 상품 재고수량을 초과하였습니다.");

        stock -= requestStock;
    }

}
