package com.attendance.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";

    private final SecretKey signingKey;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;

    public JwtTokenProvider(
            @Value("${jwt.secret:${random.uuid}${random.uuid}}") String secret,
            @Value("${jwt.access-token-expiration-minutes:30}") long accessTokenExpirationMinutes,
            @Value("${jwt.refresh-token-expiration-days:7}") long refreshTokenExpirationDays) {
        this.signingKey = buildSigningKey(secret);
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    public String generateAccessToken(Long userId, String username, String role) {
        return createToken(userId, username, role, "access", accessTokenExpirationMinutes, ChronoUnit.MINUTES);
    }

    public String generateRefreshToken(Long userId, String username, String role) {
        return createToken(userId, username, role, "refresh", refreshTokenExpirationDays, ChronoUnit.DAYS);
    }

    public String refreshAccessToken(String refreshToken) {
        Claims claims = getClaims(refreshToken);
        if (!"refresh".equals(claims.get(CLAIM_TOKEN_TYPE))) {
            throw new IllegalArgumentException("Token is not a refresh token");
        }
        return generateAccessToken(
                ((Number) claims.get(CLAIM_USER_ID)).longValue(),
                claims.getSubject(),
                claims.get(CLAIM_ROLE, String.class));
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        return ((Number) getClaims(token).get(CLAIM_USER_ID)).longValue();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getClaims(token).get(CLAIM_ROLE, String.class);
    }

    public String getTokenType(String token) {
        return getClaims(token).get(CLAIM_TOKEN_TYPE, String.class);
    }

    private String createToken(
            Long userId,
            String username,
            String role,
            String tokenType,
            long expirationAmount,
            ChronoUnit expirationUnit) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationAmount, expirationUnit);

        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey buildSigningKey(String secret) {
        byte[] decodedSecret;
        try {
            decodedSecret = Decoders.BASE64.decode(secret);
        } catch (RuntimeException exception) {
            decodedSecret = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(padSecretIfNeeded(decodedSecret));
    }

    private byte[] padSecretIfNeeded(byte[] secretBytes) {
        if (secretBytes.length >= 32) {
            return secretBytes;
        }
        byte[] paddedSecret = new byte[32];
        System.arraycopy(secretBytes, 0, paddedSecret, 0, secretBytes.length);
        return paddedSecret;
    }
}
