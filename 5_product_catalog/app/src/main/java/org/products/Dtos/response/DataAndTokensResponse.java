package org.products.Dtos.response;

import org.products.Dtos.TokenPair;
import org.products.entities.User;

public record DataAndTokensResponse(UserResponseDto user, TokenPair tokens) {
}
