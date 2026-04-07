package com.finance.demo.controller;

import com.finance.demo.dtos.request.AuthRequest;
import com.finance.demo.dtos.response.ApiResponse;
import com.finance.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/register
     * Public endpoint — registers a new user (default role: VIEWER)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse.AuthResponse> register(
            @Valid @RequestBody AuthRequest.Register request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    /**
     * POST /api/v1/auth/login
     * Public endpoint — returns JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse.AuthResponse> login(
            @Valid @RequestBody AuthRequest.Login request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}