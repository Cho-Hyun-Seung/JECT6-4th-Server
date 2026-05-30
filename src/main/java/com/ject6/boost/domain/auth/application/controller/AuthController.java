package com.ject6.boost.domain.auth.application.controller;

import com.ject6.boost.domain.auth.application.dto.OAuthLoginRequest;
import com.ject6.boost.domain.auth.application.dto.OAuthLoginResponse;
import com.ject6.boost.domain.auth.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "OAuth login", description = "OAuth authorization code login and Redis session token issuance")
    @PostMapping("/login/{provider}")
    @ResponseStatus(HttpStatus.CREATED)
    public OAuthLoginResponse login(
            @PathVariable String provider,
            @RequestBody OAuthLoginRequest request
    ) {
        return authService.login(provider, request);
    }
}
