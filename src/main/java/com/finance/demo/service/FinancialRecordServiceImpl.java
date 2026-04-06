package com.finance.demo.service;

import com.finance.backend.dto.request.FinancialRecordRequest;
import com.finance.backend.dto.response.ApiResponse.*;
import com.finance.backend.entity.*;
import com.finance.backend.exception.AppException;
import com.finance.backend.repository.*;
import com.finance.backend.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FinancialRecordResponse createRecord(FinancialRecordRequest.Create request, String createdByEmail) {

        User creator = userRepository.findByEmail(createdByEmail.trim().toLowerCase())
                .orElseThrow(() ->
                        new AppException.ResourceNotFoundException("Authenticated user not found")
                );

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory().trim())
                .transactionDate(request.getTransactionDate())
                .notes(request.getNotes())
                .createdBy(creator)
                .build();

        return toResponse(recordRepository.save(record));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FinancialRecordResponse> getRecords(
            TransactionType type,
            String category,
            LocalDate dateFrom,
            LocalDate dateTo,
            String search,
            Pageable pageable
    ) {

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new AppException.InvalidOperationException("dateFrom must not be after dateTo");
        }

        // Normalize optional inputs
        String normalizedCategory = category != null ? category.trim() : null;
        String normalizedSearch = search != null ? search.trim() : null;

        Page<FinancialRecord> page = recordRepository.findAllWithFilters(
                type,
                normalizedCategory,
                dateFrom,
                dateTo,
                normalizedSearch,
                pageable
        );

        return PagedResponse.<FinancialRecordResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialRecordResponse getRecordById(Long id) {
        return toResponse(findActiveOrThrow(id));
    }

    @Override
    @Transactional
    public FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest.Update request) {

        FinancialRecord record = findActiveOrThrow(id);

        // Partial update
        if (request.getAmount() != null) {
            record.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            record.setType(request.getType());
        }
        if (request.getCategory() != null) {
            record.setCategory(request.getCategory().trim());
        }
        if (request.getTransactionDate() != null) {
            record.setTransactionDate(request.getTransactionDate());
        }
        if (request.getNotes() != null) {
            record.setNotes(request.getNotes());
        }

        return toResponse(record); // no need to explicitly save
    }

    @Override
    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = findActiveOrThrow(id);

        if (record.isDeleted()) {
            throw new AppException.InvalidOperationException("Record already deleted");
        }

        record.setDeletedAt(LocalDateTime.now()); // soft delete
    }

    private FinancialRecord findActiveOrThrow(Long id) {
        return recordRepository.findActiveById(id)
                .orElseThrow(() -> AppException.ResourceNotFoundException.forRecord(id));
    }

    private FinancialRecordResponse toResponse(FinancialRecord r) {
        return FinancialRecordResponse.builder()
                .id(r.getId())
                .amount(r.getAmount())
                .type(r.getType())
                .category(r.getCategory())
                .transactionDate(r.getTransactionDate())
                .notes(r.getNotes())
                .createdByEmail(r.getCreatedBy().getEmail())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}