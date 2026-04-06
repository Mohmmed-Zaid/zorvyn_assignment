package com.finance.demo.dtos.request;


import com.finance.demo.entities.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

public class UserRequest {

    @Data
    public static class UpdateRole {

        @NotNull(message = "Role is required")
        private Role role;
    }

    @Data
    public static class UpdateStatus {

        @NotNull(message = "Active status is required")
        private Boolean active;
    }

    @Data
    public static class UpdateProfile {

        @Size(max = 100)
        private String fullName;

        @Email(message = "Must be a valid email address")
        private String email;
    }
}