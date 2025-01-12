package kr.hhplus.be.server.infrastructure.jpa.coupon.issuedcoupon;

import static kr.hhplus.be.server.domain.coupon.QIssuedCoupon.*;
import static kr.hhplus.be.server.domain.product.QProduct.product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.QIssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

    private final IssuedCouponJpaRepository issuedCouponJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        return issuedCouponJpaRepository.save(issuedCoupon);
    }

    @Override
    public Page<IssuedCoupon> selectIssuedCouponList(long userId, Pageable pageable) {
        List<IssuedCoupon> issuedCoupons = queryFactory
            .selectFrom(issuedCoupon)
            .where(issuedCoupon.user.id.eq(userId))
            .orderBy(issuedCoupon.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(issuedCoupon)
            .where(issuedCoupon.user.id.eq(userId))
            .fetchCount();

        return new PageImpl<>(issuedCoupons, pageable, total);
    }

    @Override
    public Optional<IssuedCoupon> findById(long issuedCouponId) {
        return issuedCouponJpaRepository.findById(issuedCouponId);
    }
}
