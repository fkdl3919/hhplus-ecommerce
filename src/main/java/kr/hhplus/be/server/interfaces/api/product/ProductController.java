package kr.hhplus.be.server.interfaces.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.info.ProductInfo;
import kr.hhplus.be.server.interfaces.api.common.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(
                schema = @Schema(implementation = PagingResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답 예제",
                    value = """
                            {
                                "item": [
                                    { "id": 1, "name": "Product1", "price": 1000 },
                                    { "id": 2, "name": "Product2", "price": 1500 }
                                ],
                                "totalCount": 100,
                                "totalPages": 10
                            }
                        """
                )
            )
        )
    })
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PageableAsQueryParam
    public ResponseEntity<PagingResponse> list(
        @ParameterObject
        @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(PagingResponse.from(productService.selectProductList(pageable)));
    }

    @Operation(summary = "인기상품 목록 조회", description = "최근 3일간 가장 많이 팔린 상위 5개 상품정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(
                schema = @Schema(implementation = ProductInfo.class),
                examples = @ExampleObject(
                    name = "성공 응답 예제",
                    value = """
                            {
                                "item": [
                                    { "id": 1, "name": "Product1", "price": 1000 },
                                    { "id": 2, "name": "Product2", "price": 1500 }
                                    { "id": 3, "name": "Product3", "price": 1500 }
                                    { "id": 4, "name": "Product4", "price": 1500 }
                                    { "id": 5, "name": "Product5", "price": 1500 }
                                ]
                            }
                        """
                )
            )
        )
    })
    @GetMapping(value = "/list/top-selling", produces = MediaType.APPLICATION_JSON_VALUE)
    @PageableAsQueryParam
    public ResponseEntity<List<ProductInfo>> list(
    ) {
        return ResponseEntity.ok(productService.selectTopSellingProductList());
    }

}
