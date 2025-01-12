package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.interfaces.common.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @Operation(summary = "주문/결제 기능", description = "주문과 결제를 요청합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "주문/결제 성공",
            content = @Content(
                schema = @Schema(implementation = PagingResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답 예제",
                    value = """
                            {
                                "item"
                            }
                        """
                )
            )
        )
    })
    @PostMapping
    public ResponseEntity<OrderResponse> order(
        @RequestBody OrderRequest orderRequest
    ){
        orderFacade.order(orderRequest.toCommand());
        return ResponseEntity.ok().build();
    }

}
