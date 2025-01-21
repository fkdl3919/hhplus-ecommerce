package kr.hhplus.be.server.interfaces.common;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity handleRuntimeException(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = SecurityException.class)
    public ResponseEntity handleSecurityException(SecurityException e) {
        log.error(e.getMessage(), e);
        // 클라이언트가 요청한 리소스에 대한 접근권한이 없음을 알림
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("사용자 인증정보가 올바르지 않습니다."));
    }

}
