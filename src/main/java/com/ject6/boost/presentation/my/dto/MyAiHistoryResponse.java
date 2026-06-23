package com.ject6.boost.presentation.my.dto;

import java.util.List;

public record MyAiHistoryResponse(
        List<MyAiHistoryItemResponse> aiHistory
) {
}
