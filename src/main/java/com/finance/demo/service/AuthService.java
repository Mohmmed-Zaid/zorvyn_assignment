package com.finance.demo.service;

import com.finance.demo.dtos.request.AuthRequest;
import com.finance.demo.dtos.response.ApiResponse.AuthResponse;
import com.finance.demo.dtos.response.ApiResponse.UserResponse;
import com.finance.demo.entities.Role;
import com.finance.demo.entities.User;
import com.finance.demo.exception.AppException;
import com.finance.demo.repository.UserRepository;
import com.finance.demo.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user with VIEWER role by default.
     */
    @Transactional
    public AuthResponse register(AuthRequest.Register request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new AppException.DuplicateResourceException(
                    "An account with email " + email + " already exists"
            );
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException.InvalidOperationException("Passwords do not match");
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)
                .active(true)
                .build();

        userRepository.save(user);

        return buildAuthResponse(jwtUtils.generateToken((UserDetails) user), user);
    }

    public AuthResponse login(AuthRequest.Login request) {

        String email = request.getEmail().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException.ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new AppException.AccessDeniedException("Account is deactivated");
        }

        return buildAuthResponse(jwtUtils.generateToken((UserDetails) user), user);
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .active(user.isActive())
                        .createdAt(user.getCreatedAt())
                        .build())
                .build();
    }
}