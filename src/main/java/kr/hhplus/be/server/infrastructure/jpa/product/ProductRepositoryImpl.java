package kr.hhplus.be.server.infrastructure.jpa.product;

import static kr.hhplus.be.server.domain.product.QProduct.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.order.QOrder;
import kr.hhplus.be.server.domain.order.QOrderItem;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JPAQueryFactory queryFactory;
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Page<Product> selectProductPaging(Pageable pageable) {
        List<Product> products = queryFactory
            .selectFrom(product)
            .orderBy(product.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(product)
            .fetchCount();

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public Optional<Product> findProductWithLock(Long id) {
        return Optional.of(productJpaRepository.findProductWithLock(id));
    }

    @Override
    public Product save(Product build) {
        return productJpaRepository.save(build);
    }

    @Override
    public List<Product> selectTopSellingProducts() {

        List<Product> products = queryFactory
            .selectFrom(product)
            .join(QOrderItem.orderItem)
                .on(
                    QOrderItem.orderItem.productId.eq(product.id)
                )
            .join(QOrder.order)
                .on(
                    QOrderItem.orderItem.order.eq(QOrder.order)
                        .and(QOrder.order.status.eq(OrderStatus.CONFIRMED))
                )
            .where(
                // 최근 3일 전부터
                QOrder.order.orderedAt.gt(LocalDateTime.now().minus(3, ChronoUnit.DAYS))
            )
            .orderBy(
                // 상품 별 수량 오름차순
                QOrderItem.orderItem.quantity.desc()
            )
            .limit(5)
            .fetch();

        return products;
    }
}
