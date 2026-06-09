package com.ject6.boost.infrastructure.common.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.application.common.exception.GlobalErrorCode;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    /**
     * 인증 사용자 정보를 기반으로 서비스 access token을 발급하는 함수.
     */
    public JwtToken issue(AuthenticatedUser user) {
        return issue(user, jwtProperties.getAccessTokenTtl(), ACCESS_TOKEN_TYPE);
    }

    /**
     * 인증 사용자 정보를 기반으로 서비스 refresh token을 발급하는 함수.
     */
    public JwtToken issueRefreshToken(AuthenticatedUser user) {
        return issue(user, jwtProperties.getRefreshTokenTtl(), REFRESH_TOKEN_TYPE);
    }

    private JwtToken issue(AuthenticatedUser user, Duration tokenTtl, String tokenType) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(tokenTtl);
        String tokenId = UUID.randomUUID().toString();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("iss", jwtProperties.getIssuer());
        payload.put("sub", String.valueOf(user.userId()));
        payload.put("jti", tokenId);
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());
        payload.put(TOKEN_TYPE_CLAIM, tokenType);
        payload.put("roles", user.roles());

        String unsignedToken = base64UrlJson(header) + "." + base64UrlJson(payload);
        String signature = sign(unsignedToken);

        return new JwtToken(
                unsignedToken + "." + signature,
                tokenId,
                tokenTtl.toSeconds()
        );
    }

    /**
     * access token을 검증하고 JWT claim에서 인증 사용자 정보를 복원하는 함수.
     */
    public AuthenticatedUser validateAccessTokenAndGetUser(String token) {
        Map<String, Object> payload = validateAndGetPayload(token, ACCESS_TOKEN_TYPE);
        return new AuthenticatedUser(
                getSubjectAsUserId(payload),
                getStringListClaim(payload, "roles")
        );
    }

    /**
     * refresh token을 검증하고 Redis refresh 세션 조회에 사용할 token id를 반환하는 함수.
     */
    public String validateRefreshTokenAndGetTokenId(String token) {
        Map<String, Object> payload = validateAndGetPayload(token, REFRESH_TOKEN_TYPE);
        Object tokenId = payload.get("jti");
        if (!(tokenId instanceof String value) || !StringUtils.hasText(value)) {
            throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
        }
        return value;
    }

    private Map<String, Object> validateAndGetPayload(String token, String expectedTokenType) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
        }

        String unsignedToken = parts[0] + "." + parts[1];
        if (!MessageDigest.isEqual(sign(unsignedToken).getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
        }

        Map<String, Object> payload = readPayload(parts[1]);
        if (!jwtProperties.getIssuer().equals(payload.get("iss"))) {
            throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
        }
        if (!expectedTokenType.equals(payload.get(TOKEN_TYPE_CLAIM))) {
            throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
        }

        long expiresAt = getLongClaim(payload, "exp");
        if (Instant.now().getEpochSecond() >= expiresAt) {
            throw new BusinessException(GlobalErrorCode.ACCESS_TOKEN_EXPIRED);
        }

        return payload;
    }

    /**
     * Map 값을 JWT payload/header에 사용할 base64url JSON 문자열로 변환하는 함수.
     */
    private String base64UrlJson(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (JsonProcessingException exception) {
            throw new BusinessException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * JWT payload 파트를 디코딩해서 claim Map으로 읽는 함수.
     */
    private Map<String, Object> readPayload(String payload) {
        try {
            byte[] decodedPayload = Base64.getUrlDecoder().decode(payload);
            return objectMapper.readValue(decodedPayload, MAP_TYPE);
        } catch (IllegalArgumentException | IOException exception) {
            throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * JWT claim에서 숫자 타입 값을 long으로 추출하는 함수.
     */
    private long getLongClaim(Map<String, Object> payload, String claimName) {
        Object value = payload.get(claimName);
        if (value instanceof Number number) {
            return number.longValue();
        }
        throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
    }

    private Long getSubjectAsUserId(Map<String, Object> payload) {
        Object subject = payload.get("sub");
        if (subject instanceof String value && StringUtils.hasText(value)) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException exception) {
                throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
            }
        }
        throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
    }

    private List<String> getStringListClaim(Map<String, Object> payload, String claimName) {
        Object value = payload.get(claimName);
        if (value instanceof List<?> values && values.stream().allMatch(String.class::isInstance)) {
            return values.stream()
                    .map(String.class::cast)
                    .toList();
        }
        throw new BusinessException(GlobalErrorCode.INVALID_TOKEN);
    }

    /**
     * HS256 방식으로 JWT 서명을 생성하는 함수.
     */
    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(requiredSecret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new BusinessException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * JWT 서명에 사용할 secret 설정값이 비어 있지 않은지 확인하고 반환하는 함수.
     */
    private String requiredSecret() {
        if (!StringUtils.hasText(jwtProperties.getSecret())) {
            throw new BusinessException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
        return jwtProperties.getSecret();
    }
}
