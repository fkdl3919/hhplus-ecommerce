package kr.hhplus.be.server.interfaces.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.application.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(
                examples = @ExampleObject(
                    name = "성공 응답 예제",
                    value = """
                            {
                                "id": 1,
                                "point": 2000
                            }
                        """
                )
            ))
    })
    @GetMapping(value = "point/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> point(

        @Parameter(description = "유저 id", example = "11")
        @PathVariable long id
    ) {
        return ResponseEntity.ok(new UserResponse(id, userFacade.getUserPoint(id)));
    }

    @Operation(summary = "잔액 충전", description = "사용자 잔액을 충전합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "충전 성공")
    })
    @PatchMapping("point/{id}")
    public ResponseEntity charge(

        @Parameter(description = "유저 id", example = "11")
        @PathVariable long id,

        @Parameter(description = "충전 금액", example = "1000")
        @RequestBody long amount

    ) {
        userFacade.chargePoint(id, amount);
        return ResponseEntity.ok().build();
    }




}
