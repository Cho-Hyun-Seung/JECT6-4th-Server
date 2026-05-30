package com.ject6.boost.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ject6.boost.domain.auth.application.dto.OAuthUserProfile;
import com.ject6.boost.domain.auth.infrastructure.OAuthRedisKeys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class RedisSessionAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

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
            authenticateWithRedisSession(authorization.substring(BEARER_PREFIX.length()));
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateWithRedisSession(String sessionToken) throws IOException {
        String session = redisTemplate.opsForValue().get(OAuthRedisKeys.SESSION_KEY_PREFIX + sessionToken);

        if (!StringUtils.hasText(session)) {
            return;
        }

        OAuthUserProfile user = objectMapper.readValue(session, OAuthUserProfile.class);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, sessionToken, List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
