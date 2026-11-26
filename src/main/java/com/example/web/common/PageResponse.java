package com.example.web.common;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/**
 * 목록 + 페이징 정보를 프론트가 쓰기 편한 형태로 감싼 공통 응답.
 */
public record PageResponse<T>(
        List<T> content,
        int page,          // 현재 페이지 (0부터 시작)
        int size,          // 페이지당 개수
        long totalElements,
        int totalPages
) {
    public static <E, T> PageResponse<T> of(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
