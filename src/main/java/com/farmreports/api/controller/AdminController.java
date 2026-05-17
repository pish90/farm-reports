package com.farmreports.api.controller;

import com.farmreports.api.dto.*;
import com.farmreports.api.dto.NoteRequest;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.AdminService;
import com.farmreports.api.service.ExportService;
import com.farmreports.api.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;
    private final ExportService exportService;

    // ── Dashboard / overview ───────────────────────────────────────────────────

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
        requireDashboardRole(auth);
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
        Integer effectiveFarmId = (isAdmin(auth) || isOpsManager(auth)) ? farmId : ClaimsHelper.getFarmId(auth);
        return ApiResponse.ok(adminService.listReports(effectiveFarmId, year, month, status));
    }

    // ── Admin report read/create ───────────────────────────────────────────────

    @GetMapping("/farms/{farmId}/report")
    public ApiResponse<ReportDto> getAdminFarmReport(
            @PathVariable Integer farmId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication auth) {
        requireDashboardRole(auth);
        return ApiResponse.ok(reportService.getReport(farmId, year, month));
    }

    @PostMapping("/farms/{farmId}/report")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReportDto> createAdminFarmReport(
            @PathVariable Integer farmId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication auth) {
        requireDashboardRole(auth);
        return ApiResponse.ok(reportService.createOrGetReport(farmId, year, month, ClaimsHelper.getUserId(auth)));
    }

    @GetMapping("/reports/{id}")
    public ApiResponse<ReportDto> getAdminReport(
            @PathVariable Integer id,
            Authentication auth) {
        requireDashboardRole(auth);
        return ApiResponse.ok(reportService.getReportById(id, null, "ADMIN"));
    }

    // ── Admin report edit ──────────────────────────────────────────────────────

    @PutMapping("/reports/{id}/attendance")
    public ApiResponse<Void> adminUpsertAttendance(
            @PathVariable Integer id,
            @RequestParam Integer farmId,
            @Valid @RequestBody List<@Valid AttendanceEntryRequest> entries,
            Authentication auth) {
        requireAdmin(auth);
        reportService.upsertAttendance(id, farmId, entries);
        return ApiResponse.ok();
    }

    @PutMapping("/reports/{id}/livestock")
    public ApiResponse<Void> adminUpsertLivestock(
            @PathVariable Integer id,
            @RequestParam Integer farmId,
            @Valid @RequestBody List<@Valid LivestockEntryRequest> entries,
            Authentication auth) {
        requireAdmin(auth);
        reportService.upsertLivestock(id, farmId, entries);
        return ApiResponse.ok();
    }

    @PutMapping("/reports/{id}/milk")
    public ApiResponse<Void> adminUpsertMilk(
            @PathVariable Integer id,
            @RequestParam Integer farmId,
            @Valid @RequestBody List<@Valid MilkEntryRequest> entries,
            Authentication auth) {
        requireAdmin(auth);
        reportService.upsertMilk(id, farmId, entries);
        return ApiResponse.ok();
    }

    @PutMapping("/reports/{id}/expenses")
    public ApiResponse<Void> adminUpsertExpenses(
            @PathVariable Integer id,
            @RequestParam Integer farmId,
            @Valid @RequestBody List<@Valid ExpenseEntryRequest> entries,
            Authentication auth) {
        requireExpenseRole(auth);
        reportService.upsertExpenses(id, farmId, entries);
        return ApiResponse.ok();
    }

    @PutMapping("/reports/{id}/attendance-notes")
    public ApiResponse<Void> adminUpsertAttendanceNotes(
            @PathVariable Integer id,
            @RequestParam Integer farmId,
            @Valid @RequestBody NoteRequest request,
            Authentication auth) {
        requireAdmin(auth);
        reportService.upsertAttendanceNotes(id, farmId, request);
        return ApiResponse.ok();
    }

    @PutMapping("/reports/{id}/livestock-notes")
    public ApiResponse<Void> adminUpsertLivestockNotes(
            @PathVariable Integer id,
            @RequestParam Integer farmId,
            @Valid @RequestBody NoteRequest request,
            Authentication auth) {
        requireAdmin(auth);
        reportService.upsertLivestockNotes(id, farmId, request);
        return ApiResponse.ok();
    }

    @PostMapping("/reports/{id}/submit")
    public ApiResponse<ReportDto> adminSubmitReport(
            @PathVariable Integer id,
            @RequestParam Integer farmId,
            Authentication auth) {
        requireDashboardRole(auth);
        return ApiResponse.ok(reportService.submitReport(id, farmId));
    }

    @PostMapping("/reports/{id}/reopen")
    public ApiResponse<ReportDto> adminReopenReport(
            @PathVariable Integer id,
            Authentication auth) {
        requireAdmin(auth);
        return ApiResponse.ok(reportService.adminReopenReport(id));
    }

    // ── Excel export ───────────────────────────────────────────────────────────

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportAllFarmsExcel(
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication auth) {
        requireAdmin(auth);

        List<FarmLiveStatusDto> statuses = adminService.getFarmLiveStatus(year, month);
        List<ExportService.FarmReport> entries = statuses.stream()
                .filter(s -> s.reportId() != null)
                .map(s -> new ExportService.FarmReport(
                        s.farmName(),
                        reportService.getReportById(s.reportId(), null, "ADMIN")))
                .toList();

        byte[] excel = exportService.generateAllFarmsExcel(year, month, entries);
        String filename = "farm-reports-" + year + "-" + String.format("%02d", month) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    // ── Access helpers ─────────────────────────────────────────────────────────

    private void requireDashboardRole(Authentication auth) {
        String role = ClaimsHelper.getRole(auth);
        if (!"ADMIN".equals(role) && !"MANAGER".equals(role) && !"OPERATIONS_MANAGER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Dashboard access requires ADMIN, MANAGER, or OPERATIONS_MANAGER role");
        }
    }

    private void requireExpenseRole(Authentication auth) {
        String role = ClaimsHelper.getRole(auth);
        if (!"ADMIN".equals(role) && !"OPERATIONS_MANAGER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Admin or Operations Manager access required");
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

    private boolean isOpsManager(Authentication auth) {
        return "OPERATIONS_MANAGER".equals(ClaimsHelper.getRole(auth));
    }
}
