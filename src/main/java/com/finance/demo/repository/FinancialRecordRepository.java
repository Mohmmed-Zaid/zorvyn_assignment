package com.finance.demo.repository;


import com.finance.demo.entities.FinancialRecord;
import com.finance.demo.entities.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    @Query("SELECT r FROM FinancialRecord r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<FinancialRecord> findActiveById(@Param("id") Long id);

    /**
     * Flexible filter query — all params optional.
     */
    @Query("""
            SELECT r FROM FinancialRecord r
            WHERE r.deletedAt IS NULL
              AND (:type IS NULL OR r.type = :type)
              AND (:category IS NULL OR LOWER(r.category) = LOWER(:category))
              AND (:dateFrom IS NULL OR r.transactionDate >= :dateFrom)
              AND (:dateTo IS NULL OR r.transactionDate <= :dateTo)
              AND (:search IS NULL
                   OR LOWER(r.notes) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(r.category) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<FinancialRecord> findAllWithFilters(
            @Param("type") TransactionType type,
            @Param("category") String category,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type AND r.deletedAt IS NULL")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("""
            SELECT r.category, COALESCE(SUM(r.amount), 0)
            FROM FinancialRecord r
            WHERE r.deletedAt IS NULL
            GROUP BY r.category
            ORDER BY SUM(r.amount) DESC
            """)
    List<Object[]> sumGroupedByCategory();

    @Query("""
            SELECT YEAR(r.transactionDate), MONTH(r.transactionDate), r.type, SUM(r.amount)
            FROM FinancialRecord r
            WHERE r.deletedAt IS NULL AND r.transactionDate >= :since
            GROUP BY YEAR(r.transactionDate), MONTH(r.transactionDate), r.type
            ORDER BY YEAR(r.transactionDate), MONTH(r.transactionDate)
            """)
    List<Object[]> monthlyTrend(@Param("since") LocalDate since);

    @Query("""
            SELECT r FROM FinancialRecord r
            WHERE r.deletedAt IS NULL
            ORDER BY r.transactionDate DESC, r.createdAt DESC
            """)
    List<FinancialRecord> findRecentActivity(Pageable pageable);
}