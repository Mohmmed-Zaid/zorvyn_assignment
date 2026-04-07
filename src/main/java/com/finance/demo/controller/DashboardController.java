package com.finance.demo.controller;

import com.finance.demo.dtos.response.ApiResponse.DashboardSummary;
import com.finance.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/v1/dashboard/summary — ANALYST, ADMIN
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}