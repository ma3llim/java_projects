package org.products.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.products.Dtos.TokenPair;
import org.products.config.properties.JwtProperties;
import org.products.entities.User;
import org.products.enums.TokenType;
import org.products.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public TokenPair generateToken(User user) {
        Instant now = Instant.now();
        Instant accessExpiry = now.plusSeconds(jwtProperties.getAccessTtlSeconds());
        Instant refreshExpiry = now.plusSeconds(jwtProperties.getRefreshTtlSeconds());

        String accessToken = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .notBefore(Date.from(now))
                .expiration(Date.from(accessExpiry))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("type", "access")
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .notBefore(Date.from(now))
                .expiration(Date.from(refreshExpiry))
                .claim("type", "refresh")
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessExpiredAt(accessExpiry)
                .refreshExpiredAt(refreshExpiry)
                .build();
    }

    public Claims validateToken(String token, TokenType tokenType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtProperties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String actualType = claims.get("type", String.class);
            if (!tokenType.name().toLowerCase().equals(actualType)) {
                throw new CustomException("Invalid token type", HttpStatus.UNAUTHORIZED);
            }

            return claims;
        } catch (ExpiredJwtException e) {
            throw new CustomException("Token expired", HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new CustomException("Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    public String extractEmail(String token) {
        return parseToken(token).get("email", String.class);
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
