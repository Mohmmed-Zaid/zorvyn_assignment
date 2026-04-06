package com.finance.demo.dtos.response;

import com.finance.demo.entities.Role;
import com.finance.demo.entities.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ApiResponse {

    @Data
    @Builder
    public static class AuthResponse {
        private String token;
        private String tokenType;
        private UserResponse user;
    }

    @Data
    @Builder
    public static class UserResponse {
        private Long id;
        private String fullName;
        private String email;
        private Role role;
        private boolean active;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class FinancialRecordResponse {
        private Long id;
        private BigDecimal amount;
        private TransactionType type;
        private String category;
        private LocalDate transactionDate;
        private String notes;
        private String createdByEmail;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    public static class DashboardSummary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netBalance;
        private List<CategoryTotal> categoryTotals;
        private List<MonthlyTrend> monthlyTrends;
        private List<FinancialRecordResponse> recentActivity;
    }

    @Data
    @Builder
    public static class CategoryTotal {
        private String category;
        private BigDecimal total;
    }

    @Data
    @Builder
    public static class MonthlyTrend {
        private int year;
        private int month;
        private TransactionType type;
        private BigDecimal total;
    }

    @Data
    @Builder
    public static class PagedResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }

    @Data
    @Builder
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private LocalDateTime timestamp;
        private Map<String, String> fieldErrors;
    }
}