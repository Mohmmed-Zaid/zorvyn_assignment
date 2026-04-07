package com.finance.demo.service;

import com.finance.demo.dtos.response.ApiResponse.*;
import com.finance.demo.entities.TransactionType;
import com.finance.demo.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository recordRepository;

    private static final int RECENT_ACTIVITY_LIMIT = 10;
    private static final int TREND_MONTHS = 6;

    /**
     * Runs all queries in a single read-only transaction.
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardSummary getSummary() {

        BigDecimal totalIncome = safe(recordRepository.sumByType(TransactionType.INCOME));
        BigDecimal totalExpenses = safe(recordRepository.sumByType(TransactionType.EXPENSE));

        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(totalIncome.subtract(totalExpenses))
                .categoryTotals(buildCategoryTotals())
                .monthlyTrends(buildMonthlyTrends())
                .recentActivity(buildRecentActivity())
                .build();
    }

    private List<CategoryTotal> buildCategoryTotals() {
        return recordRepository.sumGroupedByCategory().stream()
                .map(row -> CategoryTotal.builder()
                        .category((String) row[0])
                        .total(safe((BigDecimal) row[1]))
                        .build())
                .toList();
    }

    private List<MonthlyTrend> buildMonthlyTrends() {
        LocalDate since = LocalDate.now().minusMonths(TREND_MONTHS);

        return recordRepository.monthlyTrend(since).stream()
                .map(row -> MonthlyTrend.builder()
                        .year(((Number) row[0]).intValue())
                        .month(((Number) row[1]).intValue())
                        .type((TransactionType) row[2])
                        .total(safe((BigDecimal) row[3]))
                        .build())
                .toList();
    }

    private List<FinancialRecordResponse> buildRecentActivity() {
        return recordRepository.findRecentActivity(PageRequest.of(0, RECENT_ACTIVITY_LIMIT))
                .stream()
                .map(r -> FinancialRecordResponse.builder()
                        .id(r.getId())
                        .amount(r.getAmount())
                        .type(r.getType())
                        .category(r.getCategory())
                        .transactionDate(r.getTransactionDate())
                        .notes(r.getNotes())
                        .createdByEmail(r.getCreatedBy().getEmail())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .build())
                .toList();
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}