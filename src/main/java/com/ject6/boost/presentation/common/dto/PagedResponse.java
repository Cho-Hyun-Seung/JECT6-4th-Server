package com.ject6.boost.presentation.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        List<T> items,
        long total,
        int page,
        int size
) {
    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize()
        );
    }

    public static <T> PagedResponse<T> of(List<T> allItems, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int fromIndex = Math.min((safePage - 1) * safeSize, allItems.size());
        int toIndex = Math.min(fromIndex + safeSize, allItems.size());
        return new PagedResponse<>(
                allItems.subList(fromIndex, toIndex),
                allItems.size(),
                safePage,
                safeSize
        );
    }
}
