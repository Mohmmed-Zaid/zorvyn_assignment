package com.finance.demo.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    /**
     * POST /api/v1/records — ANALYST, ADMIN
     */
    @PostMapping
    public ResponseEntity<FinancialRecordResponse> createRecord(
            @Valid @RequestBody FinancialRecordRequest.Create request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordService.createRecord(request, userDetails.getUsername()));
    }


    @GetMapping
    public ResponseEntity<PagedResponse<FinancialRecordResponse>> getRecords(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate") String sort,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {

        // Limit page size to prevent abuse
        int safeSize = Math.min(size, 100);

        Pageable pageable = PageRequest.of(
                page,
                safeSize,
                Sort.by(direction, sort)
        );

        return ResponseEntity.ok(
                recordService.getRecords(type, category, dateFrom, dateTo, search, pageable)
        );
    }

    /**
     * GET /api/v1/records/{id} — Authenticated users
     */
    @GetMapping("/{id}")
    public ResponseEntity<FinancialRecordResponse> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    /**
     * PUT /api/v1/records/{id} — ADMIN only
     */
    @PutMapping("/{id}")
    public ResponseEntity<FinancialRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequest.Update request
    ) {
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    /**
     * DELETE /api/v1/records/{id} — ADMIN only (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
