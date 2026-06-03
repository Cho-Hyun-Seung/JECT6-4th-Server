package com.ject6.boost.common.security.authentication;

import java.util.List;

public record AuthenticatedUser(
        Long userId,
        List<String> roles
) {
}
