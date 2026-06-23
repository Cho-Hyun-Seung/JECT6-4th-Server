package com.ject6.boost.presentation.common.dto;

import java.util.List;
import org.springframework.data.domain.Pageable;

public record SimplePageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int size,
        int number
) {
    public static <T> SimplePageResponse<T> of(List<T> source, Pageable pageable) {
        int size = Math.max(pageable.getPageSize(), 1);
        int number = Math.max(pageable.getPageNumber(), 0);
        int start = Math.min(number * size, source.size());
        int end = Math.min(start + size, source.size());
        int totalPages = (int) Math.ceil((double) source.size() / size);

        return new SimplePageResponse<>(
                source.subList(start, end),
                source.size(),
                totalPages,
                size,
                number
        );
    }
}
