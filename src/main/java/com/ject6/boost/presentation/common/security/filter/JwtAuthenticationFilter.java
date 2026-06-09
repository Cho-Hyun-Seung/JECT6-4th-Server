package com.ject6.boost.presentation.common.security.filter;

import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.presentation.common.security.handler.SecurityErrorResponseWriter;
import com.ject6.boost.infrastructure.common.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityErrorResponseWriter securityErrorResponseWriter;

    /**
     * Authorization 헤더의 access token을 검증하고 SecurityContext에 인증 정보를 저장하는 함수.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (SecurityContextHolder.getContext().getAuthentication() == null
                && StringUtils.hasText(authorization)
                && authorization.startsWith(BEARER_PREFIX)) {
            try {
                authenticateWithAccessToken(authorization.substring(BEARER_PREFIX.length()));
            } catch (BusinessException e) {
                securityErrorResponseWriter.write(response, e.getErrorCode());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * access token을 검증하고 JWT claim에서 인증 사용자 정보를 복원하는 함수.
     */
    private void authenticateWithAccessToken(String accessToken) {
        AuthenticatedUser user = jwtTokenProvider.validateAccessTokenAndGetUser(accessToken);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, accessToken, toAuthorities(user.roles()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<SimpleGrantedAuthority> toAuthorities(List<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
