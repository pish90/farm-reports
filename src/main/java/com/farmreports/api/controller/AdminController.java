package com.farmreports.api.controller;

import com.farmreports.api.dto.ApiResponse;
import com.farmreports.api.dto.FarmLiveStatusDto;
import com.farmreports.api.dto.FarmSummaryDto;
import com.farmreports.api.dto.ReportDto;
import com.farmreports.api.dto.ResetPasswordRequest;
import jakarta.validation.Valid;
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

    @GetMapping("/live-status")
    public ApiResponse<List<FarmLiveStatusDto>> getLiveStatus(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication auth) {
        requireAdmin(auth);
        int y = year  != null ? year  : java.time.LocalDate.now().getYear();
        int m = month != null ? month : java.time.LocalDate.now().getMonthValue();
        return ApiResponse.ok(adminService.getFarmLiveStatus(y, m));
    }

    @PutMapping("/users/reset-password")
    public ApiResponse<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            Authentication auth) {
        requireAdmin(auth);
        adminService.resetUserPassword(request.email(), request.newPassword());
        return ApiResponse.ok(null);
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

    private void requireAdmin(Authentication auth) {
        if (!"ADMIN".equals(ClaimsHelper.getRole(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }

    private boolean isAdmin(Authentication auth) {
        return "ADMIN".equals(ClaimsHelper.getRole(auth));
    }
}
