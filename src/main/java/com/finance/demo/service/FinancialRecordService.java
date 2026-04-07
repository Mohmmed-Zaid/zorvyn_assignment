package com.finance.demo.service;

import com.finance.demo.dtos.request.FinancialRecordRequest;
import com.finance.demo.dtos.response.ApiResponse.FinancialRecordResponse;
import com.finance.demo.dtos.response.ApiResponse.PagedResponse;
import com.finance.demo.entities.TransactionType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface FinancialRecordService {

    FinancialRecordResponse createRecord(FinancialRecordRequest.Create request, String createdByEmail);

    PagedResponse<FinancialRecordResponse> getRecords(
            TransactionType type,
            String category,
            LocalDate dateFrom,
            LocalDate dateTo,
            String search,
            Pageable pageable
    );

    FinancialRecordResponse getRecordById(Long id);

    FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest.Update request);

    void deleteRecord(Long id);
}