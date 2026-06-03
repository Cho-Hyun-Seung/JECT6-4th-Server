package com.ject6.boost.domain.user.domain.entity;

import com.ject6.boost.domain.user.domain.constant.CategoryType;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class UserCategoryId implements Serializable {

    private Long user;
    private CategoryType categoryType;
}