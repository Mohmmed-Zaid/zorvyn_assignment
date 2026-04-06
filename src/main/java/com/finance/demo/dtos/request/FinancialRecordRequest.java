package com.finance.demo.dtos.request;


import com.finance.demo.entities.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialRecordRequest {

    @Data
    public static class Create {

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be positive")
        @Digits(integer = 13, fraction = 2)
        private BigDecimal amount;

        @NotNull(message = "Transaction type is required")
        private TransactionType type;

        @NotBlank(message = "Category is required")
        @Size(max = 100)
        private String category;

        @NotNull(message = "Transaction date is required")
        @PastOrPresent(message = "Transaction date cannot be in the future")
        private LocalDate transactionDate;

        @Size(max = 500)
        private String notes;
    }

    @Data
    public static class Update {

        @DecimalMin(value = "0.01")
        @Digits(integer = 13, fraction = 2)
        private BigDecimal amount;

        private TransactionType type;

        @Size(max = 100)
        private String category;

        @PastOrPresent
        private LocalDate transactionDate;

        @Size(max = 500)
        private String notes;
    }
}