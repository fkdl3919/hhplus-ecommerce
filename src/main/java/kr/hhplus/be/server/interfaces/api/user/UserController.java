package kr.hhplus.be.server.interfaces.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.interfaces.api.user.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/user")
public class UserController {



    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "User Not Found"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "서버오류")
    })
    @GetMapping("point/{id}")
    public ResponseEntity<UserResponse> point(

        @Parameter(description = "유저 id", example = "11")
        @PathVariable long id
    ) {
        return null;
    }

    @Operation(summary = "잔액 충전", description = "사용자 잔액을 충전합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "충전 성공"),
        @ApiResponse(responseCode = "404", description = "User Not Found"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "서버오류")
    })
    @PostMapping("point/{id}")
    public ResponseEntity<UserResponse> charge(

        @Parameter(description = "유저 id", example = "11")
        @PathVariable long id,

        @Parameter(description = "충전 금액", example = "11")
        @RequestBody long amount

    ) {
        return null;
    }


}
