package com.farmreports.api.controller;

import com.farmreports.api.dto.ApiResponse;
import com.farmreports.api.dto.FarmSummaryDto;
import com.farmreports.api.dto.ReportDto;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/farms")
    public ApiResponse<List<FarmSummaryDto>> getFarmSummaries(Authentication auth) {
        requireDashboardRole(auth);
        return ApiResponse.ok(adminService.getAllFarmSummaries());
    }

    @GetMapping("/reports")
    public ApiResponse<List<ReportDto>> listReports(
            @RequestParam(required = false) Integer farmId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String status,
            Authentication auth) {
        requireDashboardRole(auth);
        // Non-admin scope to own farm when no farmId specified
        Integer effectiveFarmId = isAdmin(auth) ? farmId : ClaimsHelper.getFarmId(auth);
        return ApiResponse.ok(adminService.listReports(effectiveFarmId, year, month, status));
    }

    private void requireDashboardRole(Authentication auth) {
        String role = ClaimsHelper.getRole(auth);
        if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Dashboard access requires ADMIN or MANAGER role");
        }
    }

    private boolean isAdmin(Authentication auth) {
        return "ADMIN".equals(ClaimsHelper.getRole(auth));
    }
}
