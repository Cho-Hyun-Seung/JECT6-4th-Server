package com.ject6.boost.domain.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
    FOOD("음식"),
    BEAUTY("뷰티"),
    CULTURE("문화"),
    TRAVEL("여행"),
    TECH_IT("테크/it"),
    PET("펫"),
    LIVING("생활용품"),
    FASHION("패션"),
    ETC("기타(도서/취미/기타)");

    private final String label;
}