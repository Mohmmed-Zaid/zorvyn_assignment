package com.finance.demo.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Data;

public class AuthRequest {

    @Data
    public static class Login {

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class Register {

        @NotBlank(message = "Full name is required")
        @Size(max = 100)
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
    }
}