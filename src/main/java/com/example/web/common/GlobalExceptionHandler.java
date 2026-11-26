package com.example.web.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 모든 컨트롤러에서 발생한 예외를 한 곳에서 처리해 일정한 JSON 형식으로 응답한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 서비스에서 직접 던진 예외 (404, 409 등) */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        return ResponseEntity.status(e.getStatus())
                .body(ErrorResponse.of(e.getStatus().value(), e.getMessage()));
    }

    /** @Valid 검증 실패 → 400 + 필드별 에러 메시지 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, "입력값을 확인하세요.", errors));
    }

    /** 잘못된 JSON, 존재하지 않는 enum 값 등 */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "요청 형식이 올바르지 않습니다."));
    }

    /** 그 외 예상 못한 에러 → 500 (상세 내용은 서버 로그에서 확인) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        // 404(없는 주소), 405(지원하지 않는 메서드)처럼 Spring이 상태코드를 이미 알고 있는 예외는 그대로 전달
        if (e instanceof org.springframework.web.ErrorResponse springError) {
            int code = springError.getStatusCode().value();
            return ResponseEntity.status(code).body(ErrorResponse.of(code, e.getMessage()));
        }
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "서버 오류가 발생했습니다."));
    }
}
