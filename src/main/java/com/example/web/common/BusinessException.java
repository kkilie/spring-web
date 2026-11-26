package com.example.web.common;

import org.springframework.http.HttpStatus;

/**
 * 서비스에서 "이건 사용자 잘못" 같은 상황에 던지는 예외.
 * GlobalExceptionHandler가 잡아서 HTTP 상태코드 + 메시지로 응답한다.
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(HttpStatus.NOT_FOUND, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(HttpStatus.CONFLICT, message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
