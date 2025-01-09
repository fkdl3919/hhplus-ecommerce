package kr.hhplus.be.server.infrastructure.jpa.product;

import static kr.hhplus.be.server.domain.product.QProduct.*;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.QProduct;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> selectProductList(Pageable pageable) {
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
}
