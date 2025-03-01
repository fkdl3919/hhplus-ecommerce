package kr.hhplus.be.server.interfaces.api.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.auth.AuthUser;
import kr.hhplus.be.server.auth.UserProvider;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.interfaces.api.common.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "쿠폰 발급", description = "선착순으로 쿠폰을 요청합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "쿠폰 요청 성공")
    })
    @PostMapping("request/{id}")
    public ResponseEntity requestCoupon(
        @Parameter(description = "쿠폰 userId", example = "1", name = "userId")
        @PathVariable long id,

        @Parameter(description = "유저 userId", example = "1", name = "userId")
        @UserProvider AuthUser authUser) {

        couponService.requestCoupon(id, authUser.id());
        return  ResponseEntity.ok().build();
    }

    @Operation(summary = "보유쿠폰 목록 조회", description = "보유쿠폰 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(
                schema = @Schema(implementation = PagingResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답 예제",
                    value = """
                            {
                                "item": [
                                    { "userId": 1, "userId": 1, "couponId": 1, "discountRate": 10, "status": "NOT_USED" },
                                ],
                                "totalCount": 100,
                                "totalPages": 10
                            }
                        """
                )
            )
        )
    })
    @GetMapping(value = "/issued/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PageableAsQueryParam
    public ResponseEntity<PagingResponse> list(
        @Parameter(description = "유저 userId", example = "1", name = "userId")
        @UserProvider AuthUser authUser,

        @ParameterObject
        @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(PagingResponse.from(couponService.selectIssuedCouponList(authUser.id(), pageable)));
    }

}
