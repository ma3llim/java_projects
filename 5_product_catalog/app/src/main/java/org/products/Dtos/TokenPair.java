package org.products.Dtos;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TokenPair {
    private String accessToken;
    private String refreshToken;
    private Instant accessExpiredAt;
    private Instant refreshExpiredAt;
}
