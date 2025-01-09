package kr.hhplus.be.server.interfaces.api.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.domain.coupon.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade couponFacade;

    @Operation(summary = "쿠폰 발급", description = "선착순으로 쿠폰을 발급받습니다.")
    @PostMapping("issue/{id}")
    public ResponseEntity issueCoupon(
        @Parameter(description = "쿠폰 id", example = "1")
        @PathVariable long id,

        @Parameter(description = "유저 id", example = "1")
        @RequestBody long userId) {

        couponFacade.issueCoupon(id, userId);
        return  ResponseEntity.ok().build();

    }

}
