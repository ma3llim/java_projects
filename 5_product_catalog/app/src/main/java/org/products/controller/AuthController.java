package org.products.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.products.Dtos.request.LoginRequestDto;
import org.products.Dtos.request.RequestDto;
import org.products.Dtos.response.ApiResponse;
import org.products.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication and authorization")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Login",
            security = {}
    )
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody RequestDto user, HttpServletRequest request) {
        return ResponseEntity.ok(authService.registerUser(user, request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            security = {}
    )
    public ResponseEntity<ApiResponse<?>> loginUser(@Valid @RequestBody LoginRequestDto user, HttpServletRequest request) {
        return ResponseEntity.ok(authService.loginUser(user, request));
    }
}
