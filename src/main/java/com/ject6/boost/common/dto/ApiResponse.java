package com.ject6.boost.common.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String errorCode;
    private String message;
    private String responseTime;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .responseTime(LocalDateTime.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> failure(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .responseTime(LocalDateTime.now().toString())
                .build();
    }
}
