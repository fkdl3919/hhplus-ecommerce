package kr.hhplus.be.server.infrastructure.jpa.coupon;

import static kr.hhplus.be.server.domain.coupon.QIssuedCoupon.issuedCoupon;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.infrastructure.jpa.coupon.issuedcoupon.IssuedCouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final IssuedCouponJpaRepository issuedCouponJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Coupon> findByIdWithLock(long id) {
        return Optional.of(couponJpaRepository.findByIdWithLock(id));
    }

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return couponJpaRepository.findById(couponId);
    }


    @Override
    public IssuedCoupon saveIssuedCoupon(IssuedCoupon issuedCoupon) {
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
    public Optional<IssuedCoupon> findIssuedCouponById(Long issuedCouponId) {
        if (issuedCouponId == null) return Optional.empty();
        return issuedCouponJpaRepository.findById(issuedCouponId);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }
}
