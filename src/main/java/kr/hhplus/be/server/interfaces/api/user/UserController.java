package kr.hhplus.be.server.interfaces.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/user")
public class UserController {


    @GetMapping("point/{id}")
    public ResponseEntity<Integer> point(
        @PathVariable long id
    ) {
        return pointService.selectPoint(id);
    }


}
