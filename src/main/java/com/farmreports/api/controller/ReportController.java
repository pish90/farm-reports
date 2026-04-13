package com.farmreports.api.controller;

import com.farmreports.api.dto.*;
import com.farmreports.api.security.ClaimsHelper;
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
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ExportService exportService;

    @GetMapping
    public ApiResponse<ReportDto> getReport(
            @RequestParam Integer farmId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(reportService.getReport(farmId, year, month));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReportDto> createReport(
            @Valid @RequestBody CreateReportRequest request, Authentication auth) {
        checkFarmAccess(request.farmId(), auth);
        return ApiResponse.ok(reportService.createOrGetReport(
                request.farmId(), request.year(), request.month(),
                ClaimsHelper.getUserId(auth)));
    }

    @PutMapping("/{id}/attendance")
    public ApiResponse<Void> upsertAttendance(
            @PathVariable Integer id,
            @Valid @RequestBody List<@Valid AttendanceEntryRequest> entries,
            Authentication auth) {
        reportService.upsertAttendance(id, ClaimsHelper.getFarmId(auth), entries);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/livestock")
    public ApiResponse<Void> upsertLivestock(
            @PathVariable Integer id,
            @Valid @RequestBody List<@Valid LivestockEntryRequest> entries,
            Authentication auth) {
        reportService.upsertLivestock(id, ClaimsHelper.getFarmId(auth), entries);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/milk")
    public ApiResponse<Void> upsertMilk(
            @PathVariable Integer id,
            @Valid @RequestBody List<@Valid MilkEntryRequest> entries,
            Authentication auth) {
        reportService.upsertMilk(id, ClaimsHelper.getFarmId(auth), entries);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/expenses")
    public ApiResponse<Void> upsertExpenses(
            @PathVariable Integer id,
            @Valid @RequestBody List<@Valid ExpenseEntryRequest> entries,
            Authentication auth) {
        reportService.upsertExpenses(id, ClaimsHelper.getFarmId(auth), entries);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<ReportDto> submitReport(@PathVariable Integer id, Authentication auth) {
        return ApiResponse.ok(reportService.submitReport(id, ClaimsHelper.getFarmId(auth)));
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportDto> getReportById(@PathVariable Integer id, Authentication auth) {
        return ApiResponse.ok(reportService.getReportById(
            id, ClaimsHelper.getFarmId(auth), ClaimsHelper.getRole(auth)));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportReport(@PathVariable Integer id, Authentication auth) {
        ReportDto report = reportService.getReportById(
            id, ClaimsHelper.getFarmId(auth), ClaimsHelper.getRole(auth));
        byte[] excel = exportService.generateExcel(report);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"farm-report-" + report.year() + "-" +
                String.format("%02d", report.month()) + ".xlsx\"")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excel);
    }

    private void checkFarmAccess(Integer farmId, Authentication auth) {
        if (!farmId.equals(ClaimsHelper.getFarmId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
