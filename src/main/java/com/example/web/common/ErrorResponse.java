package com.example.web.common;

import java.util.Map;

/**
 * 에러 응답 형식 통일: { status, message, fieldErrors }
 * fieldErrors 예: { "email": "이메일 형식이 올바르지 않습니다." }
 */
public record ErrorResponse(
        int status,
        String message,
        Map<String, String> fieldErrors
) {
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, Map.of());
    }
}
