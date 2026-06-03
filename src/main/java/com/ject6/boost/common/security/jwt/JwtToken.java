package com.ject6.boost.common.security.jwt;

public record JwtToken(
        String token,
        String id,
        long expiresIn
) {
}
