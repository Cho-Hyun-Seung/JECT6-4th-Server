package com.ject6.boost.infrastructure.common.security.jwt;

public record JwtToken(
        String token,
        String id,
        long expiresIn
) {
}
