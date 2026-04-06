package com.finance.demo.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<UserResponse> result = userService.getAllUsers(
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return ResponseEntity.ok(
                PagedResponse.<UserResponse>builder()
                        .content(result.getContent())
                        .page(result.getNumber())
                        .size(result.getSize())
                        .totalElements(result.getTotalElements())
                        .totalPages(result.getTotalPages())
                        .last(result.isLast())
                        .build()
        );
    }

    /**
     * GET /api/v1/users/{id} — ADMIN only
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * PATCH /api/v1/users/{id}/role — ADMIN only
     */
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest.UpdateRole request
    ) {
        return ResponseEntity.ok(userService.updateRole(id, request));
    }

    /**
     * PATCH /api/v1/users/{id}/status — ADMIN only
     */
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest.UpdateStatus request
    ) {
        return ResponseEntity.ok(userService.updateStatus(id, request));
    }

    /**
     * GET /api/v1/me — Authenticated users
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                userService.getMyProfile(userDetails.getUsername())
        );
    }
}