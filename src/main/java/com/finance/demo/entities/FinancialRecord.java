package com.finance.demo.entities;

import com.finance.demo.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single financial entry (income or expense).
 *
 * Design notes:
 * - Uses BigDecimal for precision
 * - Soft delete via deletedAt
 * - category is flexible (no strict enum)
 * - createdBy maintains audit trace
 */
@Entity
@Table(
        name = "financial_records",
        indexes = {
                @Index(name = "idx_record_date", columnList = "transactionDate"),
                @Index(name = "idx_record_type", columnList = "type"),
                @Index(name = "idx_record_category", columnList = "category"),
                @Index(name = "idx_record_deleted", columnList = "deletedAt")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Soft delete timestamp.
     * NULL = active, NOT NULL = deleted
     */
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}