package org.products.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.products.Dtos.TokenPair;
import org.products.Dtos.request.LoginRequestDto;
import org.products.Dtos.request.RequestDto;
import org.products.Dtos.response.ApiResponse;
import org.products.Dtos.response.DataAndTokensResponse;
import org.products.Dtos.response.UserResponseDto;
import org.products.entities.RefreshToken;
import org.products.entities.User;
import org.products.enums.Role;
import org.products.exceptions.CustomException;
import org.products.repository.RefreshTokenRepository;
import org.products.repository.UserRepository;
import org.products.utils.JwtUtil;
import org.products.utils.RequestUtil;
import org.products.utils.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;


    public ApiResponse<?> registerUser(RequestDto user, HttpServletRequest request) {
        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new CustomException("Email is already taken", HttpStatus.CONFLICT);
        }

        // hashing the password
        String hashedPassword = securityUtil.encodePassword(user.getPassword());
        User newUser = User.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .password(hashedPassword)
                .role(Role.valueOf(user.getRole()))
                .isActive(true)
                .build();

        userRepository.save(newUser);

        // generate tokens
        TokenPair tokenPair = jwtUtil.generateToken(newUser);

        String ipAddress = RequestUtil.getClientIpAddress(request);
        String userAgent = RequestUtil.getUserAgent(request);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(newUser.getId())
                .token(tokenPair.getRefreshToken())
                .expiresAt(tokenPair.getRefreshExpiredAt())
                .revoked(false)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(newUser.getId())
                .fullName(newUser.getFullName())
                .email(newUser.getEmail())
                .role(newUser.getRole())
                .lastLogin(newUser.getLastLogin())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();

        refreshTokenRepository.save(refreshToken);
        DataAndTokensResponse dataAndTokensResponse = new DataAndTokensResponse(userResponseDto, tokenPair);
        return ApiResponse.success(dataAndTokensResponse, "User register successfully");
    }

    public ApiResponse<?> loginUser(@Valid LoginRequestDto user, HttpServletRequest request) {
        // Check if email already exists
        User existingUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new CustomException("User Not Found", HttpStatus.NOT_FOUND));

        if (!existingUser.getIsActive()) {
            throw new CustomException("Account is disabled", HttpStatus.FORBIDDEN);
        }

        if (!securityUtil.checkPassword(user.getPassword(), existingUser.getPassword())) {
            throw new CustomException("Invalid username or password", HttpStatus.NOT_FOUND);
        }

        // Build list of authorities (roles/permissions) from your User entity
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + existingUser.getRole()));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                existingUser,
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        TokenPair tokenPair = jwtUtil.generateToken(existingUser);
        existingUser.setLastLogin(LocalDateTime.now());
        userRepository.save(existingUser);

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(existingUser.getId())
                .fullName(existingUser.getFullName())
                .email(existingUser.getEmail())
                .role(existingUser.getRole())
                .lastLogin(existingUser.getLastLogin())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(existingUser.getUpdatedAt())
                .build();
        DataAndTokensResponse dataAndTokensResponse = new DataAndTokensResponse(userResponseDto, tokenPair);

        return ApiResponse.success(dataAndTokensResponse, "User Login successfully");
    }
}
